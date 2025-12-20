package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostPublishedEventListener {

    private final KafkaPostProducer kafkaProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostPublished(PostEventDto event) {
        log.info("PostPublishedEventListener: Received event for postId={}", event.postId());
        try {
            kafkaProducer.publishPostCreate(event);
            log.info("PostPublishedEventListener: Successfully sent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send to Kafka: postId={}", event.postId(), e);
        }
    }
}
