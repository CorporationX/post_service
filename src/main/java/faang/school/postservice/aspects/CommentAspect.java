package faang.school.postservice.aspects;

import faang.school.event.AnalyticsCommentEvent;
import faang.school.postservice.annotations.PublishCommentEvent;
import faang.school.event.Event;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.publisher.comment.AnalyticsCommentEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class CommentAspect {

    private final Map<Class<? extends Event>, EventPublisher> eventPublisherMap;

    public CommentAspect(AnalyticsCommentEventPublisher analyticsPublisher) {
        eventPublisherMap = Map.of(AnalyticsCommentEvent.class, analyticsPublisher);
    }

    @AfterReturning(pointcut = "@annotation(publishCommentEvent)", returning = "result")
    public void publishEvent(JoinPoint joinPoint, PublishCommentEvent publishCommentEvent, Object result) {
        if (result == null) {
            log.info("Method returned {} null, event will not be published.", joinPoint.getSignature().getName());
            return;
        }
        Arrays.stream(publishCommentEvent.events()).forEach(eventClass -> {
            EventPublisher publisher = eventPublisherMap.get(eventClass);
            publisher.publishEvent(result);
        });
    }
}
