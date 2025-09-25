package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class KafkaProducer<T> {
    protected final ObjectMapper objectMapper;
    protected final KafkaTemplate<String, String> kafkaTemplate;

    public abstract void sendEvent(T event);

    protected void sendTo(String topic, T event) {
        try {
            String data = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, data);
            log.info("Sent new event to Kafka: topic={}, data: {}", topic, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
