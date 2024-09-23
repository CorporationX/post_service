package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic,String message) {
        log.info("Sending message {} to topic {}.", message, topic);
        CompletableFuture<SendResult<String, Object>> future
                = kafkaTemplate.send(topic, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message {} was sent to {} topic with result {}",
                        message, topic, result.getRecordMetadata().toString());
            } else {
                log.info("Message was not sent due to exception {}.", ex.getMessage());
            }
        });
    }
}
