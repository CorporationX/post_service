package faang.school.postservice.listener.kafka;

import faang.school.postservice.model.event.kafka.PostLikeEventKafka;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "${spring.kafka.topics.like}", groupId = "${spring.kafka.consumer.group-id}")
public class KafkaPostLikeConsumer {

    private final PostCacheService postCacheService;

    @KafkaHandler
    public void handlePostLike(PostLikeEventKafka event, Acknowledgment ack) {
        log.info("Starting processing of PostLikeEventKafka with Post ID: {}", event.getLike().getPostId());

        try {
            postCacheService.addPostLike(event.getLike());
            log.info("Successfully processed PostLikeEventKafka with Post ID: {}", event.getLike().getPostId());
        } catch (NoSuchElementException e) {
            log.warn("Post not found in Redis for ID: {}. Skipping...", event.getLike().getPostId());
        } catch (Exception e) {
            log.error("Failed to process PostLikeEventKafka for Post ID: {} due to unexpected error: {}",
                    event.getLike().getPostId(), e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}
