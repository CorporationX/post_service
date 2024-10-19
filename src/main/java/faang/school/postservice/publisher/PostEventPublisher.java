package faang.school.postservice.publisher;

import faang.school.postservice.model.event.PostEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ChannelTopic postEventTopic;
    private final NewTopic postPublishTopic;

    public void publish(PostEvent event) {
        redisTemplate.convertAndSend(postEventTopic.getTopic(), event);
    }

    public void publishByKafka(PostEvent event) {
        kafkaTemplate.send(postPublishTopic.name(), event);
    }
}
