package faang.school.postservice.component.post.event_produser;

import faang.school.postservice.exception.KafkaPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    protected CompletableFuture<Void> sendEvent(String topic, T event) {
        return kafkaTemplate.send(topic, generateEventKey(event), event)
                .thenAccept(result ->
                        log.info("Successfully sent event to topic: topic={}, partition={}, offset={}, event={}",
                        topic, result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(), event))
                .exceptionally(throwable -> {
                    log.error("Failed to send event to topic: topic={}, error={}", topic, throwable.getMessage());
                    throw new KafkaPublishException("Failed to send event to " + topic, throwable);
                });
    }

    protected abstract String generateEventKey(T event);
}
