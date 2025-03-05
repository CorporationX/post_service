package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.NewCommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCommentEvent(NewCommentEvent event) {
        try {
            kafkaTemplate.send("new-comments", event);
            log.info("Sent comment event for post {}", event.getPostId());
        } catch (Exception e) {
            log.error("Error sending comment event for post {}", event.getPostId(), e);
            throw e;
        }
    }
}