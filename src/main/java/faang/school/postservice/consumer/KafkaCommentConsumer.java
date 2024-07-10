package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.CommentKafkaEvent;
import faang.school.postservice.exception.LockBusyException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final RedisPostRepository redisPostRepository;
    private final CommentMapper commentMapper;

    private final ExpirableLockRegistry lockRegistry;

    @Value("${spring.data.redis.post-lock-key}")
    private String redisPostLockKey;

    @Value("${spring.data.redis.post-comments-max}")
    private int maxCommentsInRedisPost;

    @KafkaListener(topics = "${spring.data.kafka.topics.comments.name}", groupId = "${spring.data.kafka.consumer.group-id}")
    public void listenCommentEvent(CommentKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Comment event received. Comment ID: {}, Author ID: {}, Post ID: {}",
                event.getId(), event.getAuthorId(), event.getPostId());
        PostRedis foundPost = redisPostRepository.findById(event.getPostId()).orElse(null);

        if (foundPost != null) {
            log.info("Post found. Try to lock");
            Lock lock = lockRegistry.obtain(redisPostLockKey);
            if (lock.tryLock()) {
                log.info("Lock in Comment event");
                try {
                    CommentRedis comment = commentMapper.fromKafkaEventToRedis(event);
                    if (foundPost.getComments() == null) {
                        log.info("No comments in Redis post. Add last comment");
                        TreeSet<CommentRedis> comments = new TreeSet<>(Comparator.comparing(CommentRedis::getUpdatedAt).reversed());
                        comments.add(comment);
                        foundPost.setComments(comments);
                    } else {
                        foundPost.getComments().add(comment);
                        while (foundPost.getComments().size() > maxCommentsInRedisPost) {
                            foundPost.getComments().remove(foundPost.getComments().last());
                        }
                    }
                    redisPostRepository.save(foundPost);
                } finally {
                    lock.unlock();
                    log.info("Unlock comment event. Send acknowledge to Kafka");
                    acknowledgment.acknowledge();
                }
            } else {
                String errMessage = "Lock is busy. Comment Kafka Event not proceed";
                log.warn(errMessage);
                throw new LockBusyException(errMessage);
            }
        } else {
            acknowledgment.acknowledge();
            log.info("Post not found in Redis. Send acknowledge to Kafka");
        }
    }
}
