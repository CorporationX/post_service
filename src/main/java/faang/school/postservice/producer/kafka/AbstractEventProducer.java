package faang.school.postservice.producer.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@AllArgsConstructor
public abstract class AbstractEventProducer<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private String topicName;

    public void sendEvent(T event) {
        log.info("event {} was send to kafka topic {}", event, topicName);
        kafkaTemplate.send(topicName, event);
    }
}