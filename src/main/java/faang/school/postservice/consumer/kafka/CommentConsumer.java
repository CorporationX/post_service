package faang.school.postservice.consumer.kafka;

import faang.school.postservice.publisher.kafka.events.PostCommentEvent;
import faang.school.postservice.service.redis.CachedPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentConsumer {
    private final CachedPostService cachedPostService;

    @KafkaListener(topics = "${spring.kafka.topic-name.post-comments}", groupId = "comment-group")
    public void consume(PostCommentEvent event, Acknowledgment acknowledgment) {
        log.info("Received PostCommentEvent: {}", event);
        try {
            cachedPostService.addCommentToCachedPost(event.getCommentNewsFeedDto().getPostId(), event.getCommentNewsFeedDto());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing PostCommentEvent: {}", event, e);
        }
    }
}