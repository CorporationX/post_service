package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.NewLikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLikeEvent(NewLikeEvent event) {
        try {
            kafkaTemplate.send("new-likes", event);
            log.info("Sent like event for post {}", event.getPostId());
        } catch (Exception e) {
            log.error("Error sending like event for post {}", event.getPostId(), e);
            throw e;
        }
    }
}