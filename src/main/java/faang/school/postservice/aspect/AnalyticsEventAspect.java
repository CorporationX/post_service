package faang.school.postservice.aspect;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.event.AnalyticsEvent;
import faang.school.postservice.event.EventType;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostViewEventPublisher;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AnalyticsEventAspect {
    private final PostViewEventPublisher postViewEventPublisher;
    private final UserContext userContext;

    @AfterReturning(pointcut = "@annotation(analyticsEvent)", returning = "post")
    public void publishPostViewEvent(JoinPoint joinPoint, AnalyticsEvent analyticsEvent, Post post) {
        if (analyticsEvent.value() == EventType.POST_VIEW) {
            Long viewerId = getViewerId();
            postViewEventPublisher.publish(post, viewerId);
        }
    }

    private Long getViewerId() {
        userContext.setUserId(1);
        Long viewerId = userContext.getUserId();
        return viewerId;
    }
}
