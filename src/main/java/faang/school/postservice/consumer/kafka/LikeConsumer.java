package faang.school.postservice.consumer.kafka;

import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.publisher.kafka.events.PostLikeEvent;
import faang.school.postservice.service.redis.CachedPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeConsumer {
    private final CachedPostService cachedPostService;

    @KafkaListener(topics = "${spring.kafka.topic-name.post-likes}", groupId = "like-group")
    public void consume(PostLikeEvent event, Acknowledgment acknowledgment) {
        log.info("Received PostLikeEvent: {}", event);
        try {
            if (event.getLikeAction() == LikeAction.ADD) {
                cachedPostService.incrementPostLikes(event.getPostId());
            } else {
                cachedPostService.decrementPostLikes(event.getPostId());
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing PostLikeEvent: {}", event, e);
        }
    }
}