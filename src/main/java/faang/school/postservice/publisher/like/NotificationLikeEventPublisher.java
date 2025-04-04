package faang.school.postservice.publisher.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.event.NotificationLikeEvent;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationLikeEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final LikeMapper likeMapper;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.notification-like-topic.name}")
    private String notificationLikeTopic;

    @Override
    public void publishEvent(Object dto) {
        NotificationLikeEvent event = likeMapper.toNotificationLikeEvent((Like) dto);
        event.setAuthorId(((Like) dto).getPost().getAuthorId());
        log.info("Publishing event like postId{} to Kafka:", event.getPostId());
        try {
            String jsonEvents = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(notificationLikeTopic, jsonEvents);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize NotificationLikeEvent to JSON. Event data: {}. Error message: {}",
                    event, e.getMessage(), e);
        }
    }
}