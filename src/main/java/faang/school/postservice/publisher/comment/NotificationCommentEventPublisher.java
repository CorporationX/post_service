package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.event.NotificationCommentEvent;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationCommentEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CommentMapper commentMapper;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.notification-comment-topic.name}")
    private String notificationCommentTopicName;

    @Override
    public void publishEvent(Object dto) {
        NotificationCommentEvent event = commentMapper.toNotificationCommentEvent((Comment) dto);
        try {
            String jsonEvents = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(notificationCommentTopicName, jsonEvents);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize NotificationCommentEvent to JSON. Event data: {}. Error message: {}",
                    event, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}