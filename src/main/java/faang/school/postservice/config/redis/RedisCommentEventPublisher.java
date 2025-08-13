package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.dto.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

/**
 * RedisCommentEventPublisher — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author agent
 * @since 13.08.2025
 */

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
        // Отправляем объект напрямую
        redisTemplate.convertAndSend(commentTopic.getTopic(), event);
    }
}