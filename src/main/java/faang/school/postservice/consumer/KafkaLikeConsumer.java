package faang.school.postservice.consumer;

import faang.school.postservice.dto.publishable.fornewsfeed.FeedLikeEvent;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedUnlikeEvent;
import faang.school.postservice.service.feed.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {
    private final CacheService cacheService;

    @KafkaListener(topics = "${spring.data.kafka.topics.like.name}",
            groupId = "${spring.data.kafka.consumer.groups.like}")
    public void consume(FeedLikeEvent event) {
        Long postId = event.getPostId();
        log.info("Received FeedLikeEvent for post ID: {}", postId);

        try {
            cacheService.incrementLike(postId);
            log.info("Successfully incremented like for post ID: {}", postId);
        } catch (Exception e) {
            log.error("Failed to incremented like for post ID: {}", postId, e);
        }
    }

    @KafkaListener(topics = "${spring.data.kafka.topics.unlike.name}",
            groupId = "${spring.data.kafka.consumer.groups.like}")
    public void consumeDelete(FeedUnlikeEvent event) {
        Long postId = event.getPostId();
        log.info("Received FeedUnlikeEvent for post ID: {}", postId);

        try {
            cacheService.decrementLike(postId);
            log.info("Successfully decrement like for post ID: {}", postId);
        } catch (Exception e) {
            log.error("Failed to decrement like for post ID: {}", postId, e);
        }
    }
}
