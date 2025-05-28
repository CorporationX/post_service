package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostPublishDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventPublisher {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic topic;

    public void publish(PostPublishDto event) {
        try {
            String eventAsJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic.name(), eventAsJson);
            log.info("Event: {} sent to topic {}",eventAsJson, topic);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event", e);
            throw new RuntimeException(e);
        }
    }
}
