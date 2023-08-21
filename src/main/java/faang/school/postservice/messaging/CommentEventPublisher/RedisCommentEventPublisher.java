package faang.school.postservice.messaging.CommentEventPublisher;

import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.mapper.CommentEventMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Data
public class RedisCommentEventPublisher implements CommentEventPublisher {

    private CommentEventMapper commentEventMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChannelTopic topic;

    public RedisCommentEventPublisher() {
    }

    public RedisCommentEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public CommentEventDto publish(CommentEventDto commentEventDto) {
        Long sendCommentEventDto = redisTemplate.convertAndSend(topic.getTopic(), commentEventDto);
        return commentEventMapper.toDto(sendCommentEventDto);
    }
}
