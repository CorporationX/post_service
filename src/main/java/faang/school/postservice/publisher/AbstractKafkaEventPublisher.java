package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaEventPublisher<T> {

    protected final KafkaTemplate<String, Object> kafkaTemplate;
    protected final ObjectMapper objectMapper;
    protected final NewTopic topic;

    public void publish(T event) {
        try {
            kafkaTemplate.send(topic.name(), objectMapper.writeValueAsString(event));
            log.info("Event published: {}", event);
        } catch (JsonProcessingException e) {
            log.error("SerializationException", e);
            throw new SerializationException(e.getMessage());
        }
    }
}
