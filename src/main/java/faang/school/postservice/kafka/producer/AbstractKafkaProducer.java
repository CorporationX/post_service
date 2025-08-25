package faang.school.postservice.kafka.producer;

import faang.school.postservice.exception.KafkaEventSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

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

    protected CompletableFuture<SendResult<String, Object>> publishEventAsync(String topic, String key, T event) {
        return kafkaTemplate.send(topic, key, event)
                .thenApply(result -> {
                    log.info("Event sent to Kafka. Topic: {}, Key: {}, Event: {}", topic, key, event);
                    return result;
                })
                .exceptionally(ex -> {
                    log.error("Failed to send event to Kafka. Topic: {}, Key: {}, Event: {}", topic, key, event, ex);
                    throw new KafkaEventSendException("Kafka send failed", ex);
                });
    }
}
