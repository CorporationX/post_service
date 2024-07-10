package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostViewKafkaEvent;
import faang.school.postservice.exception.LockBusyException;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {

    private final RedisPostRepository redisPostRepository;
    private final ExpirableLockRegistry lockRegistry;

    @Value("${spring.data.redis.post-lock-key}")
    private String redisPostLockKey;

    @KafkaListener(topics = "${spring.data.kafka.topics.post_views.name}", groupId = "${spring.data.kafka.consumer.group-id}")
    public void listenPostViewEvent(PostViewKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Post-view event received. Post ID: {}", event.getPostId());

        PostRedis foundPost = redisPostRepository.findById(event.getPostId()).orElse(null);

        if (foundPost != null) {
            log.info("Post found. Try to lock");
            Lock lock = lockRegistry.obtain(redisPostLockKey);
            if (lock.tryLock()) {
                log.info("Lock in Post View event");
                try {
                    log.info("Increment views for post ID: {} in Redis", foundPost.getId());
                    foundPost.setViews(foundPost.getViews() + 1);
                    redisPostRepository.save(foundPost);
                } finally {
                    lock.unlock();
                    log.info("Unlock Post View event. Send acknowledge to Kafka");
                    acknowledgment.acknowledge();
                }
            } else {
                String errMessage = "Lock is busy. Post View Kafka Event not proceed";
                log.warn(errMessage);
                throw new LockBusyException(errMessage);
            }
        } else {
            acknowledgment.acknowledge();
            log.info("Post not found in Redis. Send acknowledge to Kafka");
        }
    }
}
