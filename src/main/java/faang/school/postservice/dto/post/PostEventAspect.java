package faang.school.postservice.dto.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.publisher.PostViewEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventAspect {
    private final PostViewEventPublisher eventPublisher;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final TaskExecutor taskExecutor;

    @After("@annotation(publishPostEvent)")
    public void publishPostEvent(ProceedingJoinPoint joinPoint, PublishPostEvent publishPostEvent) throws  Throwable {
        Object result = joinPoint.proceed();

        if (result != null) {
            if (publishPostEvent.async()) {
                taskExecutor.execute(()-> publishEvents(publishPostEvent.eventClass(), result));
            } else {
                publishEvents(publishPostEvent.eventClass(), result);
            }
        }
    }

    private void publishEvents(Class<?> eventClass, Object methodResult) {
        try {
            long userId = userContext.getUserId();
            UserDto user = userServiceClient.getUser(userId);

            if (eventClass == PostV2Dto.class) {
                publishSingleEvent((PostV2Dto) methodResult, userId);
            } else if (eventClass == PageResponse.class) {
                publishCollectionEvents((PageResponse) methodResult, userId);
            }

        } catch (Exception e) {
            log.error("Failed to publish PostViewEvent: {}", e.getMessage());
        }
    }

    private void publishSingleEvent(PostV2Dto postDto, long userId) {

            PostViewEvent event = new PostViewEvent(
                    postDto.id(),
                    postDto.authorId(),
                    userId,
                    LocalDateTime.now()
            );
            eventPublisher.publish(event);

    }

    private void publishCollectionEvents(Object methodResult, long userId) {
        if (methodResult instanceof PageResponse<?> pageResponse) {
            pageResponse.content().forEach(item -> {
                if (item instanceof PostV2Dto postDto) {
                    PostViewEvent event = new PostViewEvent(
                            postDto.id(),
                            postDto.authorId(),
                            userId,
                            LocalDateTime.now()
                    );
                    eventPublisher.publish(event);
                }
            });
        } else if (methodResult instanceof Collection<?> collection) {
            collection.forEach(item -> {
                if (item instanceof PostV2Dto postDto) {
                    PostViewEvent event = new PostViewEvent(
                            postDto.id(),
                            postDto.authorId(),
                            userId,
                            LocalDateTime.now()
                    );
                    eventPublisher.publish(event);
                }
            });
        }
    }
}
