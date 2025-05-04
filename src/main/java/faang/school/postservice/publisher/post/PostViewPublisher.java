package faang.school.postservice.publisher.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.PostViewMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.PostViewEvent;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostViewPublisher implements EventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PostViewMapper postViewMapper;

    @Value("${spring.kafka.topics.publish-post-view-topic.name}")
    private String postViewTopicName;

    @Override
    public void publishEvent(Object dto) {
        PostViewEvent event = postViewMapper.toEvent((Post) dto);
        try {
            String jsonEvents = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(postViewTopicName, jsonEvents);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize NotificationCommentEvent to JSON. Event data: {}. Error message: {}",
                    event, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
