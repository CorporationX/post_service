package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.model.LikeEvent;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.data.kafka.topic-name.likes}")
    public void listener(LikeEvent likeEvent, Acknowledgment ack) {
        log.info("Received message like [{}]", likeEvent);
        postCacheService.incrementLikes(likeEvent.postId());
        ack.acknowledge();
        log.info("Successfully received like");
    }
}
