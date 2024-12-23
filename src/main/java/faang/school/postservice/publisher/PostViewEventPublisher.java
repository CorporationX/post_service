package faang.school.postservice.publisher;

import faang.school.postservice.event.PostViewEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostViewEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public PostViewEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(PostViewEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}