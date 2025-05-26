package faang.school.postservice.component.post;

import faang.school.postservice.exception.KafkaPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    public void sendEvent(String topic, T event) {
        try {
            SendResult<String, T> result = kafkaTemplate.send(topic, generateEventKey(event), event).get();
            log.info("Successfully sent event to topic {}: {}", topic, event);
        } catch (Exception e) {
            log.error("Failed to send event to topic {}: {}", topic, e.getMessage());
            throw new KafkaPublishException("Failed to send event to " + topic, e);
        }
    }

    protected abstract String generateEventKey(T event);
}
