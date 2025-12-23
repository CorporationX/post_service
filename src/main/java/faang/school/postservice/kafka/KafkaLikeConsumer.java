package faang.school.postservice.kafka;

import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.service.like.RedisPostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final RedisPostLikeService redisLikeService;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics.like_topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeLikeEvent(LikeEvent event, Acknowledgment ack) {
        try {
            redisLikeService.addLikeToPost(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process like event: {}", e.getMessage());
        }
    }
}
