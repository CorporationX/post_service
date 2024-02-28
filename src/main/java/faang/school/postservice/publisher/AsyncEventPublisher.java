package faang.school.postservice.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public abstract class AsyncEventPublisher<T> {
    private KafkaTemplate<String, T> kafkaTemplate;
    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    protected abstract String getTopicName();

    @Async("taskExecutor")
    protected void asyncPublish(T event) {
        CompletableFuture<SendResult<String, T>> future = kafkaTemplate.send(getTopicName(), event);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("{} event was sent: {}", getTopicName(), result);
            } else {
                log.error("Failed to send {} event", getTopicName(), e);
            }
        });
    }
}