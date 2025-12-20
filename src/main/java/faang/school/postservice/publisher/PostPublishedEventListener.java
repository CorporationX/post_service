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
        try {
            kafkaProducer.publishPostCreate(event);
        } catch (Exception e) {
            log.error("Failed to send to Kafka: postId={}", event.postId(), e);
        }
    }
}
