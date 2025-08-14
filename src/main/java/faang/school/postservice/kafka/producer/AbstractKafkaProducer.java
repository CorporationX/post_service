package faang.school.postservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;


@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    protected void publishEvent(String topic, String key, T event) {
        kafkaTemplate.send(topic, key, event)
                .thenAccept(result -> log.info("Event sent to Kafka. Topic: {}, Key: {}, Event: {}", topic, key, event))
                .exceptionally(ex -> {
                    log.error("Failed to send event to Kafka. Topic: {}, Key: {}, Event: {}", topic, key, event, ex);
                    return null;
                });
    }
}
