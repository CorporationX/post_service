package faang.school.postservice.consumer.like;

import faang.school.postservice.dto.kafka.event.like.LikeEvent;
import faang.school.postservice.dto.kafka.event.like.UnlikeEvent;
import faang.school.postservice.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final RedisService redisService;

    @KafkaListener(
            topics = "${spring.kafka.topics.like-events}",
            containerFactory = "likeEventsKafkaListenerContainerFactory",
            groupId = "like-events-group"
    )
    public void consumeLikeEvent(@Payload LikeEvent likeEvent,
                                 Acknowledgment acknowledgment) {
        try {
            if (likeEvent == null || likeEvent.getPostId() == null || likeEvent.getLikeAuthorId() == null) {
                log.warn("Invalid like event: {}", likeEvent);
                acknowledgment.acknowledge();
                return;
            }

            Long postId = likeEvent.getPostId();
            Long userId = likeEvent.getLikeAuthorId();

            log.info("Processing like event: postId={}, userId={}, likeId={}",
                    postId, userId, likeEvent.getLikeId());

            CompletableFuture<Long> updateFuture = redisService.incrementPostLikes(postId, userId);

            updateFuture.whenComplete((result, error) -> {
                if (error != null) {
                    log.error("Failed to process like event for post {}: {}",
                            postId, error.getMessage());
                } else {
                    log.info("Successfully processed like event for post: {}, new count: {}",
                            postId, result);
                }
                acknowledgment.acknowledge();
            });

        } catch (Exception e) {
            log.error("Error processing like event: {}", e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.unlike-events}",
            containerFactory = "unlikeEventsKafkaListenerContainerFactory",
            groupId = "unlike-events-group"
    )
    public void consumeUnlikeEvent(@Payload UnlikeEvent unlikeEvent,
                                   Acknowledgment acknowledgment) {
        try {
            if (unlikeEvent == null || unlikeEvent.getPostId() == null || unlikeEvent.getLikeAuthorId() == null) {
                log.warn("Invalid unlike event: {}", unlikeEvent);
                acknowledgment.acknowledge();
                return;
            }

            Long postId = unlikeEvent.getPostId();
            Long userId = unlikeEvent.getLikeAuthorId();

            log.info("Processing unlike event: postId={}, userId={}, likeId={}",
                    postId, userId, unlikeEvent.getLikeId());

            CompletableFuture<Long> updateFuture = redisService.decrementPostLikes(postId, userId);

            updateFuture.whenComplete((result, error) -> {
                if (error != null) {
                    log.error("Failed to process unlike event for post {}: {}",
                            postId, error.getMessage());
                } else {
                    log.info("Successfully processed unlike event for post: {}, new count: {}",
                            postId, result);
                }
                acknowledgment.acknowledge();
            });

        } catch (Exception e) {
            log.error("Error processing unlike event: {}", e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }
}
