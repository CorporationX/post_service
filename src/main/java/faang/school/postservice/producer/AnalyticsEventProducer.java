package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.CommentEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventProducer {
    private final ObjectMapper objectMapper;
    private final KafkaProperty kafkaProperty;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendAnalyticsEvent(AnalyticsEvent event) {

        try {
            String data = objectMapper.writeValueAsString(event);
            String topic = "analiticsEvent";
            log.info("Send new event id={} to Kafka: topic={}, data: {}", event.getReceiverId(), topic, data);
            kafkaTemplate.send(topic, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Data
    @Accessors(chain = true)
    public static class AnalyticsEvent {
        private long receiverId;

        private long actorId;

        private EventType eventType;

        private LocalDateTime receivedAt;
    }

    public enum EventType {
        PROFILE_VIEW,
        PROJECT_VIEW,
        FOLLOWER,
        POST_PUBLISHED,
        POST_VIEW,
        POST_LIKE,
        POST_COMMENT,
        SKILL_RECEIVED,
        RECOMMENDATION_RECEIVED,
        ADDED_TO_FAVOURITES,
        PROJECT_INVITE,
        TASK_COMPLETED,
        GOAL_COMPLETED,
        ACHIEVEMENT_RECEIVED,
        PROFILE_APPEARED_IN_SEARCH,
        PROJECT_APPEARED_IN_SEARCH;

        public static EventType of(int type) {
            for (EventType eventType : EventType.values()) {
                if (eventType.ordinal() == type) {
                    return eventType;
                }
            }
            throw new IllegalArgumentException("Unknown event type: " + type);
        }
    }
}
