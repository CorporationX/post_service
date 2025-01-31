package faang.school.postservice.consumer;

import faang.school.postservice.event.PostLikeEvent;
import faang.school.postservice.service.redis.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostLikeConsumer {
    private final RedisCacheService redisCacheService;

    @KafkaListener(topics = "${spring.kafka.topics.post-like}")
    public void listen(PostLikeEvent event, Acknowledgment ack) {
        log.info("Received like event: {}", event);
        try {
            redisCacheService.incrementLike(event.getPostId());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error while processing like event: {}", event, e);
            throw e;
        }
    }
}
