package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class GenericKafkaProducer<T> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendEvent(String topic, String key, T event, String eventNameForLog) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, json);
            log.info("{} sent to Kafka [key={}]", eventNameForLog, key);
        } catch (Exception e) {
            log.error("Failed to send {} to Kafka", eventNameForLog, e);
        }
    }

    public void sendEvent(String topic, T event, String eventNameForLog) {
        sendEvent(topic, null, event, eventNameForLog);
    }
}
