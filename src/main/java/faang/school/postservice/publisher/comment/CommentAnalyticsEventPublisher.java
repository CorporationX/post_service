package faang.school.postservice.publisher.comment;

import faang.school.postservice.dto.event.CommentEventDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component("redisCommentAnalyticsEventPublisher")
public class CommentAnalyticsEventPublisher implements CommentEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentAnalyticsTopic;

    public CommentAnalyticsEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("commentAnalyticsTopic") ChannelTopic commentAnalyticsTopic
    ) {
        this.redisTemplate = redisTemplate;
        this.commentAnalyticsTopic = commentAnalyticsTopic;
    }

    @Override
    public void publish(CommentEventDto event) {
        redisTemplate.convertAndSend(commentAnalyticsTopic.getTopic(), event);
    }
}

