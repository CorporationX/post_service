package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractKafkaPublisher<T> {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private ObjectMapper mapper;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Retryable(retryFor = JsonProcessingException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    protected void publishKafkaEvent(T event, String topic) {
        try {
            String eventJson = mapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse message", e);
        }
    }
}
