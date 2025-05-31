package faang.school.postservice.publisher;

import faang.school.postservice.model.event.EventType;
import faang.school.postservice.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void publish(EventType eventType, Object eventPayload) {
        String topic = kafkaProperties.getTopic(eventType);
        if (topic == null) {
            log.warn("[KafkaEventPublisher] Topic for EventType={} not found!", eventType);
            return;
        }
        kafkaTemplate.send(topic, eventPayload);
        log.info("[KafkaEventPublisher] Sent event to topic {}: {}", topic, eventPayload);
    }
}
