package faang.school.postservice.producers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(T event, String topicName) {
        log.info("Sending Json event: {} to Kafka topic: {}", event, topicName);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event=[" + event +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                log.error("Unable to send event=[" +
                        event + "] due to : " + ex.getMessage());
            }
        });
    }
}
