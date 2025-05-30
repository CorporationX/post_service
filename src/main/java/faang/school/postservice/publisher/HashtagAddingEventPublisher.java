package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.HashtagAddingEvent;
import faang.school.postservice.exception.JsonSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashtagAddingEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.kafka.topic.hashtag-adding.name}")
    private String topic;

    public void publish(HashtagAddingEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message);
            log.debug("Hashtag event sending on topic {}", topic);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException("Serialization object %s in json error", event.toString());
        }
    }
}
