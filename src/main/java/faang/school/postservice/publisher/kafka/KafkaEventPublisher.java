package faang.school.postservice.publisher.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Bulgakov
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <E> void sendEvent(String topic, E event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully to topic '{}'", topic);
            } else {
                log.error("Error sending message: {}", ex.getMessage());
            }
        });
    }
}
