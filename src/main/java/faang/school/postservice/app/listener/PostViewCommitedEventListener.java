package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.PostViewKafkaProducer;
import faang.school.postservice.model.event.PostViewEvent;
import faang.school.postservice.model.event.application.PostViewCommittedEvent;
import faang.school.postservice.model.event.kafka.PostViewKafkaEvent;
import faang.school.postservice.redis.publisher.PostViewPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostViewCommitedEventListener {
    private static final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final PostViewPublisher postViewPublisher;
    private final PostViewKafkaProducer postViewKafkaProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostViewCommittedEvent(PostViewCommittedEvent event) {
        postViewPublisher.publish(createPostViewEvent(event.getPostId(), event.getPostAuthorId(), event.getViewerId()));
        postViewKafkaProducer.sendEvent(
                new PostViewKafkaEvent(event.getPostId(), event.getViewerId(), LocalDateTime.now().format(formatter)));
    }

    private PostViewEvent createPostViewEvent(Long postId, Long postAuthorId, Long viewerId) {
        return new PostViewEvent(postId, postAuthorId, viewerId, LocalDateTime.now());
    }
}
