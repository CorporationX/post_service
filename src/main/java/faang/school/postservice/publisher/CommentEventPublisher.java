package faang.school.postservice.publisher;


import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.comment.CommentEvent;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Data
public class CommentEventPublisher implements MessagePublisher<CommentEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;
    private ChannelTopic topic;

    @PostConstruct
    public void init() {
        this.topic = new ChannelTopic(redisProperties.getChannel("comment-channel"));
    }

    @Override
    public void publish(CommentEvent message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
