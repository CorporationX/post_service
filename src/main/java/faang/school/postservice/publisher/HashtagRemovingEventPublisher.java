package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.JsonSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashtagRemovingEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.hashtag-removing.name}")
    private String topic;

    public void publish(Long postId) {
        try {
            String message = objectMapper.writeValueAsString(postId);
            kafkaTemplate.send(topic, message);
            log.debug("Post id sending on topic {}", topic);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException("Serialization object %d in json error", postId);
        }
    }
}
