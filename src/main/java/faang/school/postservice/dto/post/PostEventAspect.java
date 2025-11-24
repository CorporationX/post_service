package faang.school.postservice.dto.post;

import faang.school.postservice.dto.common.PageResponse;
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
    private final Executor taskExecutor;

    public PostEventAspect(PostViewEventProducer eventPublisher,
                           @Qualifier("postEventTaskExecutor") Executor taskExecutor) {
        this.eventPublisher = eventPublisher;
        this.taskExecutor = taskExecutor;
    }

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
        extractPostDtos(methodResult)
                .forEach(postDto -> publishEventForPost(postDto, postDto.authorId()));
    }

    private Collection<PostV2Dto> extractPostDtos(Object methodResult) {
        if (methodResult instanceof PageResponse<?> pageResponse) {
            return extractFromPageResponse(pageResponse);
        } else if (methodResult instanceof Collection<?> collection) {
            return extractFromCollection(collection);
        }
        return Collections.emptyList();
    }

    private Collection<PostV2Dto> extractFromPageResponse(PageResponse<?> pageResponse) {
        return extractPostDtosFromSource(pageResponse.content());
    }

    private Collection<PostV2Dto> extractFromCollection(Collection<?> collection) {
        return extractPostDtosFromSource(collection);
    }

    private Collection<PostV2Dto> extractPostDtosFromSource(Collection<?> source) {
        return source.stream()
                .filter(PostV2Dto.class::isInstance)
                .map(PostV2Dto.class::cast)
                .collect(Collectors.toList());
    }

    private void publishEventForPost(PostV2Dto postDto, long userId) {
        PostViewEvent event = createPostViewEvent(postDto, userId);
        eventPublisher.publish(event);
    }

    private PostViewEvent createPostViewEvent(PostV2Dto postDto, long userId) {
        return new PostViewEvent(
                postDto.id(),
                postDto.authorId(),
                userId,
                LocalDateTime.now()
        );
    }
}
