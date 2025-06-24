package faang.school.postservice.consumer;

import faang.school.postservice.event.like.PostLikeEvent;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.repository.redis.LikeCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final LikeCacheRepository likeCacheRepository;

    @KafkaListener(
            topics = "${spring.kafka.topic.likes.name}",
            groupId = "${spring.kafka.consumer.group-id:likes}",
            containerFactory = "postLikeKafkaListenerFactory"
    )
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "${spring.kafka.consumer.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${spring.kafka.consumer.retry.backoff-period}")
    )
    public void listen(PostLikeEvent event, Acknowledgment ack) {
        log.info("Starting event processing: {}...", event);
        long postId = event.getPostId();

        try {
            likeCacheRepository.incrementLikesPost(postId, event.getAuthorId());
            log.info("Event {} processed successfully", event);
            ack.acknowledge();
        } catch (PostNotFoundException e) {
            log.warn("Post not found in cache. ID: {}", postId);
            ack.acknowledge();
        } catch (OptimisticLockingFailureException e) {
            log.warn("Concurrent modification error likes count for post {}", postId);
            throw e;
        } catch (Exception e) {
            log.error("Event {} processed failed", event);
            throw e;
        }
    }
}
