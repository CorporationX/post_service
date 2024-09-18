package faang.school.postservice.messaging.kafka.pulisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class AbstractKafkaPublisher<T> implements EventPublisher<T> {
    protected final KafkaTemplate<String, String> kafkaTemplate;
    protected final ObjectMapper objectMapper;
    protected final String kafkaTopic;

    @Override
    public void publish(T event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(kafkaTopic, jsonMessage);
            log.info("Message successfully sent to top {}: {}", kafkaTopic, jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("Error while sending to Kafka. Type: {}. Serialization error: {}", event.getClass(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error while sending message to Kafka topic '{}': {}", kafkaTopic, e.getMessage(), e);
        }
    }
}
