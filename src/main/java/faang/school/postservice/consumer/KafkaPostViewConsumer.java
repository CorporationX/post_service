package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {

    private final PostCacheService postCacheService;

    @KafkaListener(
            topics = "${app.kafka.topics.post_views.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "postViewEventListenerContainerFactory"
    )
    public void consume(PostViewEvent event, Acknowledgment ack) {
        try {
            long newCount = postCacheService.incrementPostViews(event.getPostId());
            if (newCount >= 0) {
                log.info("Processed PostViewEvent: postId={}, newViews={}", event.getPostId(), newCount);
            } else {
                log.info("Processed PostViewEvent: postId={} not cached", event.getPostId());
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process PostViewEvent {}", event, e);
        }
    }
}

