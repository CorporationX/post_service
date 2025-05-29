package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.service.like.RedisLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {
    private final RedisLikeService redisLikeService;

    @KafkaListener(topics = "${spring.kafka.producer.topics.like}", groupId = "like-consumer-group", concurrency = "3")
    public void consumeLikeEvent(LikeEvent event) {
        log.info("Received like for post {} (author: {})", event.getPostId(), event.getAuthorId());
        try {
            redisLikeService.incrementLikesForPost(event.getPostId());
        } catch (PostNotFoundException e) {
            log.warn("Post {} not found", event.getPostId());
        } catch (Exception e) {
            log.error("Error processing like event for post {}: {}", event.getPostId(), e.getMessage(), e);
        }
    }
}
