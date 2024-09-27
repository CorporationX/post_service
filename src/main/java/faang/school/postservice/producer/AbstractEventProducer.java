package faang.school.postservice.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@AllArgsConstructor
public class AbstractEventProducer<T> {
    protected final KafkaTemplate<String, Object> kafkaTemplate;

    protected final String topicName;

    public void sendEvent(T event) {
        kafkaTemplate.send(topicName, event);
        log.info("Event {} sent successfully to topic {}", event.toString(), topicName);
    }

    public void sendEvent(T event, String messageKey) {
        kafkaTemplate.send(topicName, messageKey, event);
        log.info("Event {} sent successfully to topic {} with messageKey {}",
                event.toString(), topicName, messageKey);
    }
}
