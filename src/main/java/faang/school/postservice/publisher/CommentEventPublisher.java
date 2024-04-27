package faang.school.postservice.publisher;

import faang.school.postservice.mapper.ToJsonCommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentEventPublisher implements MessagePublisher {
    @Value("${spring.data.redis.channels.comment.name}")
    private String commentTopic;
    private final RedisTemplate redisTemplate;
    private final ToJsonCommentMapper toJsonCommentMapper;


    @Override
    public <T> void publish(T event) {
        String json = toJsonCommentMapper.toJson(event);
        redisTemplate.convertAndSend(commentTopic, json);
    }

}
