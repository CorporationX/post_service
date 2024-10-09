package faang.school.postservice.config.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(T event) {
        String topic = getTheme();
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent message=[{}] to topic=[{}] with section=[{}] and offset=[{}]",
                            event.toString(), topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                } else {
                    log.error("Error sending message=[{}] to topic=[{}]. Error:", event.toString(), topic, ex);
                }
            });

        } catch (Exception e) {
            log.error("Error serializing message object:", e);
        }
    }

    public abstract String getTheme();
}
