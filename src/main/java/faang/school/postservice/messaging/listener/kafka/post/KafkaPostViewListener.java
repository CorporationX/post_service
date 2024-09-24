package faang.school.postservice.messaging.listener.kafka.post;

import faang.school.postservice.event.kafka.PostViewKafkaEvent;
import faang.school.postservice.messaging.listener.kafka.KafkaEventListener;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewListener implements KafkaEventListener<PostViewKafkaEvent> {
    private final RedisPostRepository redisPostRepository;

    @Override
    @KafkaListener(topics = "${spring.kafka.topic.post_views}",
            groupId = "${spring.kafka.consumer.group-id}")
    @Retryable(value = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public void onMessage(PostViewKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Post view event received. Post ID: {}", event.getPostId());

        redisPostRepository.findById(event.getPostId()).ifPresentOrElse(postRedis -> {
                    if (postRedis.getViews() == null) {
                        postRedis.setViews(new AtomicLong(0));
                    }
                    postRedis.getViews().incrementAndGet();
                    try {
                        redisPostRepository.save(postRedis);
                        acknowledgment.acknowledge();
                    } catch (OptimisticLockingFailureException e) {
                        log.error("Failed to update Post with ID: {} due to version conflict", postRedis.getId());
                        throw e;
                    }
                },
                () -> {
                    acknowledgment.acknowledge();
                    log.error("Post with ID: {} not found in Redis", event.getPostId());
                });
    }
}