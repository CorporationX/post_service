package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEvent;

import faang.school.postservice.mapper.post.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractPublisher<CommentEvent> {
    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 @Value("${spring.data.redis.channels.comment_channel.name}") String channelName,
                                 JsonMapper<CommentEvent> jsonMapper) {
        super(redisTemplate, channelName, jsonMapper);
    }
}