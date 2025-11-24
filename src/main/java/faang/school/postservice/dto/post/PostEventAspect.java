package faang.school.postservice.dto.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.producer.PostViewEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class PostEventAspect {
    private final PostViewEventProducer eventPublisher;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final Executor taskExecutor;

    public PostEventAspect(PostViewEventProducer eventPublisher,
                           UserServiceClient userServiceClient,
                           UserContext userContext,
                           @Qualifier("postEventTaskExecutor") Executor taskExecutor) {
        this.eventPublisher = eventPublisher;
        this.userServiceClient = userServiceClient;
        this.userContext = userContext;
        this.taskExecutor = taskExecutor;
    }

    @AfterReturning(pointcut = "@annotation(publishPostEvent)", returning = "result")
    public void publishPostEvent(JoinPoint joinPoint, Object result,
                                 PublishPostEvent publishPostEvent) throws Throwable {

        if (result != null) {
            long userId = userContext.getUserId();
            if (publishPostEvent.async()) {
                taskExecutor.execute(() -> publishEvents(publishPostEvent.eventClass(), result, userId));
            } else {
                publishEvents(publishPostEvent.eventClass(), result, userId);
            }
        }
    }

    private void publishEvents(Class<?> eventClass, Object methodResult, long userId) {
        try {
            if (eventClass == PostV2Dto.class) {
                PostV2Dto postDto = (PostV2Dto) methodResult;
                publishSingleEvent(postDto, userId);
            } else if (eventClass == PageResponse.class) {
                publishCollectionEvents((PageResponse) methodResult, userId);
            }
        } catch (Exception e) {
            log.error("Failed to publish PostViewEvent: {}", e.getMessage());
        }
    }

    private void publishSingleEvent(PostV2Dto postDto, long userId) {
        UserDto author = userServiceClient.getUser(postDto.authorId());

        PostViewEvent event = new PostViewEvent(
                postDto.id(),
                author,
                userId,
                LocalDateTime.now()
        );

        eventPublisher.publish(event);
    }

    private void publishCollectionEvents(PageResponse pageResponse, long userId) {
        getPostDtosFromResult(pageResponse)
                .forEach(postDto -> publishEventForPost(postDto, userId));
    }

    private Collection<PostV2Dto> getPostDtosFromResult(Object methodResult) {
        Collection<?> source = null;

        if (methodResult instanceof PageResponse<?> pageResponse) {
            source = pageResponse.content();
        } else if (methodResult instanceof Collection<?> collection) {
            source = collection;
        }

        if (source == null) {
            return Collections.emptyList();
        }

        return source.stream()
                .filter(PostV2Dto.class::isInstance)
                .map(PostV2Dto.class::cast)
                .collect(Collectors.toList());
    }

    private void publishEventForPost(PostV2Dto postDto, long userId) {
        UserDto author = userServiceClient.getUser(postDto.authorId());

        PostViewEvent event = new PostViewEvent(
                postDto.id(),
                author,
                userId,
                LocalDateTime.now()
        );
        eventPublisher.publish(event);
    }
}
