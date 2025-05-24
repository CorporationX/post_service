package faang.school.postservice.component.post;

import faang.school.postservice.exception.KafkaPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    public void sendEvent(String topic, T event) {
        CompletableFuture<SendResult<String, T>> future = kafkaTemplate.send(topic, generateEventKey(event), event);

        future.thenAccept(result -> {
            log.info("Successfully sent event to topic {}: {}", topic, event);
        }).exceptionally(throwable -> {
            log.error("Failed to send event to topic {}: {}", topic, throwable.getMessage());
            throw new KafkaPublishException("Failed to send event to " + topic, throwable);
        });
    }

    protected abstract String generateEventKey(T event);
}
