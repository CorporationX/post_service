package faang.school.postservice.listener.kafka;

import faang.school.postservice.model.event.kafka.PostViewEventKafka;
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
@KafkaListener(topics = "${spring.kafka.topics.post-view}", groupId = "${spring.kafka.consumer.group-id}")
public class KafkaPostViewConsumer {

    private final PostCacheService postCacheService;

    @KafkaHandler
    public void handlePostView(PostViewEventKafka event, Acknowledgment ack) {
        log.info("Starting processing of PostViewEventKafka with Post ID: {}", event.getPostDto().getId());

        try {
            postCacheService.addPostView(event.getPostDto());
            log.info("Successfully processed PostViewEventKafka with Post ID: {}", event.getPostDto().getId());
        } catch (NoSuchElementException e) {
            log.warn("Post not found in Redis for ID: {}. Skipping...", event.getPostDto().getId());
        } catch (Exception e) {
            log.error("Failed to process PostViewEventKafka for Post ID: {} due to unexpected error: {}",
                    event.getPostDto().getId(), e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}
