package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;

@Setter
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractKafkaProducer<T> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic topic;

    @Async("kafkaProducerConsumerExecutor")
    @Retryable(
        retryFor = {RuntimeException.class},
        maxAttemptsExpression = "${spring.data.kafka.producer.retry.maxAttempts}",
        backoff = @Backoff(delayExpression = "${spring.data.kafka.producer.retry.backOffDelay}")
    )
    public void send(String event) {
        try {
            kafkaTemplate.send(topic.name(), event);
            log.info("Publish event: {} into topic: {}", event, topic.name());
        } catch (Exception e) {
            int numberOfRetry = RetrySynchronizationManager.getContext().getRetryCount();
            String message = "Failed to publish event %s into topic: %s, effort: %d".formatted(event, topic.name(), numberOfRetry);
            log.error(message, e);
            throw new RuntimeException(e);
        }
    }

    @Recover
    public void handleFailure(RuntimeException e, T event) {
        log.error("Event {} could not be published into topic: {}, reason: {}", event, e.getMessage(), topic.name());
        throw new RuntimeException(e);
    }
}
