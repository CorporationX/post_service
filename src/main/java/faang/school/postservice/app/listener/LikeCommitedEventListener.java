package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.LikeKafkaProducer;
import faang.school.postservice.model.enums.LikePostEvent;
import faang.school.postservice.model.event.application.LikeCommitedEvent;
import faang.school.postservice.model.event.kafka.LikeKafkaEvent;
import faang.school.postservice.redis.publisher.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeCommitedEventListener {
    private final LikeEventPublisher likeEventPublisher;
    private final LikeKafkaProducer likeKafkaProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeCommittedEvent(LikeCommitedEvent event) {
        likeEventPublisher.publish(new LikePostEvent(event.getLikeAuthorId(), event.getPostId(), event.getPostAuthorId()));
        likeKafkaProducer.sendEvent(new LikeKafkaEvent(event.getLikeId(), event.getPostId()));
    }
}
