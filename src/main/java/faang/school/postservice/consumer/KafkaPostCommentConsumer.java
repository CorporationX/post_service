package faang.school.postservice.consumer;

import faang.school.postservice.event.PostCommentEvent;
import faang.school.postservice.service.redis.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostCommentConsumer {
    private final RedisCacheService redisCacheService;

    @KafkaListener(topics = "${spring.kafka.topics.post-comment}")
    public void listen(PostCommentEvent event, Acknowledgment ack) {
        log.info("Received post comment event: {}", event);
        try {
            redisCacheService.addCommentForPost(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to add post comment to redis", e);
            throw new RuntimeException("Failed to add post comment to redis");
        }
    }
}
