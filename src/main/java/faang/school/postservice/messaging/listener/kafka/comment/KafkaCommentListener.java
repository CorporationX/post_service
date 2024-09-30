package faang.school.postservice.messaging.listener.kafka.comment;

import faang.school.postservice.event.kafka.CommentKafkaEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.messaging.listener.kafka.KafkaEventListener;
import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.TreeSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentListener implements KafkaEventListener<CommentKafkaEvent> {
    private final RedisPostRepository redisPostRepository;
    private final CommentMapper commentMapper;

    @Value("${spring.data.redis.cache.capacity.max.comments}")
    private int maxCapacityComments;

    @Override
    @KafkaListener(topics = "${spring.kafka.topic.comments}",
            groupId = "${spring.kafka.consumer.group-id}")
    @Retryable(value = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public void onMessage(CommentKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Comment event received. Comment ID: {}, Author ID: {}, Post ID: {}",
                event.getId(), event.getAuthorId(), event.getPostId());

        redisPostRepository.findById(event.getPostId()).ifPresentOrElse(postRedis -> {
                    CommentRedis commentRedis = commentMapper.toCommentRedis(event);

                    if (postRedis.getComments() == null) {
                        postRedis.setComments(new TreeSet<>(Comparator
                                .comparing(CommentRedis::getUpdatedAt)
                                .reversed()));
                        log.info("Create Set in Post: {}", postRedis.getId());
                    }

                    if (postRedis.getComments().size() < maxCapacityComments) {
                        if (!postRedis.getComments().contains(commentRedis)) {
                            postRedis.getComments().add(commentRedis);
                            log.info("In Post: {} add new comment: {}", postRedis.getId(), commentRedis);
                        } else {
                            log.info("Comment already exists: {}", commentRedis);
                        }

                    } else {
                        CommentRedis pollComment = postRedis.getComments().pollFirst();
                        postRedis.getComments().add(commentRedis);
                        log.info("Comments are larger than maximum: {}, remove comment: {}, and add new comment: {}",
                                maxCapacityComments, pollComment, commentRedis);
                    }
                    log.info("Post ID to save: {}", postRedis.getId());
                    try {
                        redisPostRepository.save(postRedis);
                        log.info("Saving post with ID: {} and comments: {}", postRedis.getId(), postRedis.getComments());

                        acknowledgment.acknowledge();
                    } catch (OptimisticLockingFailureException e) {
                        log.error("Failed to update Post with ID: {} due to version conflict", postRedis.getId());
                        throw e;
                    }
                },
                () -> {
                    acknowledgment.acknowledge();
                    log.info("Post with ID: {} not found in Redis", event.getPostId());
                });

    }
}