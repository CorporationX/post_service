package faang.school.postservice.messaging.CommentEventPublisher;

import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisCommentEventPublisher implements CommentEventPublisher {
    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final ChannelTopic topic;
    @Autowired
    private  JsonMapper jsonMapper;

    public RedisCommentEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(CommentEventDto commentEventDto) {
        jsonMapper.toObject(commentEventDto)
                .ifPresent(s -> redisTemplate.convertAndSend(topic.getTopic(), s));
    }
}
