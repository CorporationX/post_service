package faang.school.postservice.aspects.post.view;

import faang.school.postservice.annotations.PublishPostEvent;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.post.PostEventType;
import faang.school.postservice.publisher.post.PostEventPublisher;
import faang.school.postservice.service.post.view.PostResultParser;
import faang.school.postservice.validation.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PostViewAspect {
    private final UserContext userContext;
    private final PostResultParser resultParser;
    private final PostValidator postValidator;
    private final PostEventPublisher eventPublisher;

    @AfterReturning(value = "@annotation(publishPostEvent)", returning = "result")
    public void publishEvent(JoinPoint joinPoint, PublishPostEvent publishPostEvent, Object result) {
        if (result == null) {
            log.info("Method {} returned null. Event not published.", joinPoint.getSignature().getName());
            return;
        }

        try {
            Long viewerId = userContext.getUserId();
            List<Post> posts = resultParser.parseResult(result);
            PostEventType[] eventTypes = publishPostEvent.eventTypes();

            posts.stream()
                    .filter(post -> !postValidator.shouldSkip(post, viewerId))
                    .forEach(post -> eventPublisher.publishEvents(post, viewerId, eventTypes));

        } catch (Exception e) {
            log.error("Failed to publish event: {}", e.getMessage());
        }
    }

}