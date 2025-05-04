package faang.school.postservice.aspects;

import faang.school.postservice.annotations.PublishPostViewEvent;
import faang.school.postservice.publisher.post.PostViewPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class PostViewAspect {
    private final PostViewPublisher publisher;

    @AfterReturning(pointcut = "@annotation(publishPostViewEvent)", returning = "result")
    public void publishEvent(JoinPoint joinPoint, PublishPostViewEvent publishPostViewEvent, Object result) {
        if (result == null) {
            log.info("Method returned {} null, event will not be published.", joinPoint.getSignature().getName());
            return;
        }
        publisher.publishEvent(result);
    }
}
