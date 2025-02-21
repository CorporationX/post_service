package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.AnalyticsCommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnalyticsCommentEventPublisher implements EventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CommentMapper commentMapper;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.analytics-comment-topic.name}")
    private String analyticsCommentTopicName;

    @Override
    public void publishEvent(Object dto) {
        AnalyticsCommentEvent event = commentMapper.toAnalyticsCommentEvent((Comment) dto);
        try {
            String jsonEvents = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(analyticsCommentTopicName, jsonEvents);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize AnalyticsCommentEvent to JSON. Event data: {}. Error message: {}",
                    event, e.getMessage(), e);
        }
    }
}