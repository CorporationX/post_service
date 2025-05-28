package faang.school.postservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    protected void publishEvent(String topic, T event) {
        kafkaTemplate.send(topic, event)
                .thenAccept(result -> log.info("✅ Event sent to Kafka. Topic: {}, Event: {}", topic, event))
                .exceptionally(ex -> {
                    log.error("Failed to send event to Kafka. Topic: {}, Event: {}", topic, event, ex);
                    return null;
                });
    }
}
