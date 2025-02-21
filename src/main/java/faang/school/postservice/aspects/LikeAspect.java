package faang.school.postservice.aspects;

import faang.school.postservice.model.event.AnalyticsLikeEvent;
import faang.school.postservice.model.event.Event;
import faang.school.postservice.model.event.NotificationLikeEvent;
import faang.school.postservice.annotations.PublishLikeEvent;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.publisher.like.AnalyticsLikeEventPublisher;
import faang.school.postservice.publisher.like.NotificationLikeEventPublisher;
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
public class LikeAspect {

    private final Map<Class<? extends Event>, List<EventPublisher>> eventPublisherMap;

    public LikeAspect(AnalyticsLikeEventPublisher analyticsPublisher, NotificationLikeEventPublisher notificationPublisher) {
        eventPublisherMap = Map.of(
                AnalyticsLikeEvent.class, List.of(analyticsPublisher),
                NotificationLikeEvent.class, List.of(notificationPublisher),
                Event.class, List.of(analyticsPublisher, notificationPublisher)
        );
    }

    @AfterReturning(pointcut = "@annotation(publishLikeEvent)", returning = "result")
    public void publishEvent(JoinPoint joinPoint, PublishLikeEvent publishLikeEvent, Object result) {
        if (result == null) {
            log.info("Method returned {} null, event will not be published.", joinPoint.getSignature().getName());
            return;
        }
        Arrays.stream(publishLikeEvent.events()).forEach(eventClass -> {
            List<EventPublisher> publishers = eventPublisherMap.get(eventClass);
            if (publishers != null) {
                publishers.forEach(publisher -> publisher.publishEvent(result));
            } else {
                log.warn("No publisher found for event type: {}", eventClass.getSimpleName());
            }
        });
    }
}
