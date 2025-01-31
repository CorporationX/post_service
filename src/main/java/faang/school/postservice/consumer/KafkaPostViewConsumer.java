package faang.school.postservice.consumer;

import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.service.redis.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {
    private final RedisCacheService redisCacheService;

    @KafkaListener(topics="${spring.kafka.topics.post-view}")
    public void listen(PostViewEvent event, Acknowledgment ack) {
        log.info("Received post view event: {}", event);
        try {
            redisCacheService.incrementPostViews(event.getPostId());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error while updating post view", e);
            throw e;
        }
    }
}
