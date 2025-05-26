package faang.school.postservice.component.post.event_produser;

import faang.school.postservice.exception.KafkaPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    protected CompletableFuture<Void> sendEvent(String topic, T event) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SendResult<String, T> result = kafkaTemplate.send(topic, generateEventKey(event), event).get();
                log.info("Successfully sent event to topic: topic={}, partition={}, offset={}, event={}",
                        topic, result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(), event);
                return null;
            } catch (Exception e) {
                log.error("Failed to send event to topic: topic={}, error={}", topic, e.getMessage());
                throw new KafkaPublishException("Failed to send event to " + topic, e);
            }
        });
    }

    protected abstract String generateEventKey(T event);
}
