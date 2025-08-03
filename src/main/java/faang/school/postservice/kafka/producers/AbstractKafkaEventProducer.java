package faang.school.postservice.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaEventProducer<T> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    protected abstract String getTopic();

    public void sendEvent(T event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(getTopic(), message);
            log.info("Sent Kafka message to [{}]: {}", getTopic(), message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event for topic [{}]", getTopic(), e);
            throw new RuntimeException(e);
        }
    }
}
