package faang.school.postservice.kafka;

import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.service.like.RedisPostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final RedisPostLikeService redisLikeService;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics.like_topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(LikeEvent event) {
        redisLikeService.addLikeToPost(event);
    }
}
