package faang.school.postservice.config.redis;

import faang.school.postservice.config.redis.dto.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisCommentEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentTopic;

    public RedisCommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                      ChannelTopic commentTopic) {
        this.redisTemplate = redisTemplate;
        this.commentTopic = commentTopic;
        log.info("✅ RedisCommentEventPublisher создан с RedisTemplate={} и ChannelTopic={}",
                redisTemplate, commentTopic.getTopic());
    }

    public void publish(CommentEvent event) {
        log.info("📢 Публикация CommentEvent в топик '{}': {}", commentTopic.getTopic(), event);
        redisTemplate.convertAndSend(commentTopic.getTopic(), event);
    }
}