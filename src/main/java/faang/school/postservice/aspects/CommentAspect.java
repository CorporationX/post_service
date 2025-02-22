package faang.school.postservice.aspects;

import faang.school.postservice.model.event.AnalyticsCommentEvent;
import faang.school.postservice.annotations.PublishCommentEvent;
import faang.school.postservice.model.event.Event;
import faang.school.postservice.model.event.NotificationCommentEvent;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.publisher.comment.AnalyticsCommentEventPublisher;
import faang.school.postservice.publisher.comment.NotificationCommentEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class CommentAspect {

    private final Map<Class<? extends Event>, List<EventPublisher>> eventPublisherMap;

    public CommentAspect(AnalyticsCommentEventPublisher analyticsPublisher, NotificationCommentEventPublisher notificationPublisher) {
        eventPublisherMap = Map.of(
                AnalyticsCommentEvent.class, List.of(analyticsPublisher),
                NotificationCommentEvent.class, List.of(notificationPublisher),
                Event.class , List.of(analyticsPublisher, notificationPublisher)
        );
    }

    @AfterReturning(pointcut = "@annotation(publishCommentEvent)", returning = "result")
    public void publishEvent(JoinPoint joinPoint, PublishCommentEvent publishCommentEvent, Object result) {
        if (result == null) {
            log.info("Method returned {} null, event will not be published.", joinPoint.getSignature().getName());
            return;
        }
        Arrays.stream(publishCommentEvent.events()).forEach(eventClass -> {
            List<EventPublisher> publishers = eventPublisherMap.get(eventClass);
            if (publishers != null) {
                publishers.forEach(publisher -> publisher.publishEvent(result));
            } else {
                log.warn("No publisher found for event type: {}", eventClass.getSimpleName());
            }
        });
    }
}
