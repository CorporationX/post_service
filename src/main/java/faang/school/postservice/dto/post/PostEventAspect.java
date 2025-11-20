package faang.school.postservice.dto.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.kafka.publisher.PostViewEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
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
    private final TaskExecutor taskExecutor;
    private final ObjectMapper objectMapper;

    @AfterReturning(pointcut = "@annotation(publishPostEvent)", returning = "result")
    public void publishPostEvent(JoinPoint joinPoint, Object result,
                                 PublishPostEvent publishPostEvent) throws Throwable {

        if (result != null) {
            if (publishPostEvent.async()) {
                taskExecutor.execute(() -> publishEvents(publishPostEvent.eventClass(), result));
            } else {
                publishEvents(publishPostEvent.eventClass(), result);
            }
        }
    }

    private void publishEvents(Class<?> eventClass, Object methodResult) {
        try {
            if (eventClass == PostV2Dto.class) {
                PostV2Dto postDto = (PostV2Dto) methodResult;
                publishSingleEvent(postDto, postDto.authorId());
            } else if (eventClass == PageResponse.class) {
                publishCollectionEvents((PageResponse) methodResult);
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

    private void publishCollectionEvents(Object methodResult) {
        if (methodResult instanceof PageResponse<?> pageResponse) {
            pageResponse.content().forEach(item -> {
                if (item instanceof PostV2Dto postDto) {
                    PostViewEvent event = new PostViewEvent(
                            postDto.id(),
                            postDto.authorId(),
                            postDto.authorId(),
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
                            postDto.authorId(),
                            LocalDateTime.now()
                    );
                    eventPublisher.publish(event);
                }
            });
        }
    }
}
