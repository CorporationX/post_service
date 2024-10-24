package faang.school.postservice.publis.aspect.like;

import faang.school.postservice.model.Like;
import faang.school.postservice.publis.publisher.like.LikeEventPublisher;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class LikeEventAspect {
    private final LikeEventPublisher likeEventPublisher;

    @AfterReturning(
            pointcut = "@annotation(faang.school.postservice.annotation.like.NotificationEvent) && args(.., like)",
            returning = "result", argNames = "result,like")
    public void publishLikeEvent(Object result, Like like) {
        likeEventPublisher.publishPostLikeEventToBroker(like);
    }
}
