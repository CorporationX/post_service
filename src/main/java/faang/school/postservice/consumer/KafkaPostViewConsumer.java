package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumer {

    private final StringRedisTemplate redisTemplate;

    private static final String VIEWS_KEY_SUFFIX = ":views";

    @KafkaListener(
            topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group.name}"
    )
    public void consume(PostViewEvent event, Acknowledgment ack) {
        if (event == null || event.postId() == null) {
            log.warn("Invalid PostViewEvent: {}", event);
            ack.acknowledge();
            return;
        }


        redisTemplate.opsForValue().increment(getPostViewsKey(event.postId()));
        ack.acknowledge();
    }

    private String getPostViewsKey(Long postId) {
        return "post:" + postId + VIEWS_KEY_SUFFIX;
    }
}