package faang.school.postservice.aspects.post.view;

import faang.school.postservice.annotations.PublishPostEvent;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.event.Event;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.post.factory.PostViewEventFactory;
import faang.school.postservice.service.event.PostViewEventBuffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PostViewAspect {
    private final UserContext userContext;
    private final PostViewEventFactory postViewEventFactory;
    private final PostViewEventBuffer postViewEventBuffer;

    @AfterReturning(value = "@annotation(publishPostEvent)", returning = "result")
    public void publishEvent(JoinPoint joinPoint, PublishPostEvent publishPostEvent, Object result) {
        if (result == null) {
            log.info("Method returned {} null, event will not be published.", joinPoint.getSignature().getName());
            return;
        }
        try {
            Long viewerId = userContext.getUserId();
            List<Post> posts = parseResult(result);
            Class<? extends Event>[] eventTypes = publishPostEvent.events();

            posts.stream()
                    .filter(post -> !shouldSkip(post, viewerId))
                    .forEach(post -> {
                        postViewEventFactory.createEvents(post, viewerId, eventTypes)
                                .forEach(postViewEventBuffer::add);
                    });
        } catch (Exception e) {
            log.warn("No publisher found for event {}", publishPostEvent, e);
        }
    }

    private List<Post> parseResult(Object result) {
        if (result instanceof Post) {
            return List.of((Post) result);
        } else if (result instanceof List<?> list) {
            return list.stream()
                    .filter(Post.class::isInstance)
                    .map(Post.class::cast)
                    .toList();
        }
        return Collections.emptyList();
    }

    private boolean shouldSkip(Post post,Long viewerId) {
        return post.isDeleted() || post.getAuthorId() == null || post.getAuthorId().equals(viewerId);
    }
}
