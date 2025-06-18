package faang.school.postservice.publisher.comment;

import faang.school.postservice.dto.event.CommentEventDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component("redisCommentEventPublisher")
public class RedisCommentEventPublisher implements CommentEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentTopic;

    public RedisCommentEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("commentTopic") ChannelTopic commentTopic
    ) {
        this.redisTemplate = redisTemplate;
        this.commentTopic = commentTopic;
    }

    @Override
    public void publish(CommentEventDto event) {
        redisTemplate.convertAndSend(commentTopic.getTopic(), event);
    }
}
