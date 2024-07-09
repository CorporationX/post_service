package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostViewKafkaEvent;
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

    @Value("${spring.data.redis.lock-key}")
    private String redisLockKey;

    @KafkaListener(topics = "${spring.data.kafka.topics.post_views.name}", groupId = "${spring.data.kafka.consumer.group-id}")
    public void listenPostViewEvent(PostViewKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Post-view event received. Post ID: {}", event.getPostId());

        PostRedis foundPost = redisPostRepository.findById(event.getPostId()).orElse(null);

        if (foundPost != null) {
            Lock lock = lockRegistry.obtain(redisLockKey);
            if (lock.tryLock()) {
                try {
                    log.info("Increment views for post ID: {} in Redis", foundPost.getId());
                    foundPost.setViews(foundPost.getViews() + 1);
                    redisPostRepository.save(foundPost);
                } finally {
                    lock.unlock();
                }
            }
        }
        acknowledgment.acknowledge();
    }
}
