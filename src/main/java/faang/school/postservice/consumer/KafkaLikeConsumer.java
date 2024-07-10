package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.LikeKafkaEvent;
import faang.school.postservice.exception.LockBusyException;
import faang.school.postservice.model.redis.LikeRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final RedisPostRepository redisPostRepository;
    private final ExpirableLockRegistry lockRegistry;

    @Value("${spring.data.redis.post-lock-key}")
    private String redisPostLockKey;

    @KafkaListener(topics = "${spring.data.kafka.topics.likes.name}", groupId = "${spring.data.kafka.consumer.group-id}")
    public void listenLikeEvent(LikeKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Like event received. Author ID: {}, Post ID: {}", event.getAuthorId(), event.getPostId());
        PostRedis foundPost = redisPostRepository.findById(event.getPostId()).orElse(null);

        if (foundPost != null) {
            log.info("Post found. Try to lock");
            Lock lock = lockRegistry.obtain(redisPostLockKey);
            if (lock.tryLock()) {
                log.info("Lock in Like event");
                try {
                    LikeRedis like = LikeRedis.builder()
                            .userId(event.getAuthorId())
                            .build();
                    if (foundPost.getLikes() == null) {
                        foundPost.setLikes(List.of(like));
                    } else {
                        foundPost.getLikes().add(like);
                    }
                    redisPostRepository.save(foundPost);
                } finally {
                    lock.unlock();
                    log.info("Unlock like event. Send acknowledge to Kafka");
                    acknowledgment.acknowledge();
                }
            } else {
                String errMessage = "Lock is busy. Like Kafka Event not proceed";
                log.warn(errMessage);
                throw new LockBusyException(errMessage);
            }
        } else {
            acknowledgment.acknowledge();
            log.info("Post not found in Redis. Send acknowledge to Kafka");
        }
    }
}
