package faang.school.postservice.consumer.kafka;

import faang.school.postservice.publisher.kafka.events.PostViewEvent;
import faang.school.postservice.service.redis.CachedPostService;
import faang.school.postservice.service.redis.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostViewConsumer {
    private final CachedPostService cachedPostService;

    @KafkaListener(topics = "${spring.kafka.topic-name.post-views}", groupId = "view-group")
    public void consume(PostViewEvent event, Acknowledgment acknowledgment) {
        log.info("Received PostViewEvent: {}", event);
        try {
            cachedPostService.addPostView(event.getPostId(), event.getViews());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing PostViewEvent: {}", event, e);
        }
    }
}
