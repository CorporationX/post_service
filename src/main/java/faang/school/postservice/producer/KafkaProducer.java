package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class KafkaProducer<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public void sendEvent(T event) {
        kafkaTemplate.send(topic, event);
        log.info("Event sent to topic {}", topic);
    }
}
