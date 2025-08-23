package faang.school.postservice.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommentEventPublisherImpl implements CommentEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(CommentEventPublisherImpl.class);
    private static final String COMMENT_TOPIC = "comment.events";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void publishCommentEvent(CommentEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(COMMENT_TOPIC, message);
            logger.info("Published comment event for post {} by user {}",
                    event.postId(), event.commentAuthorId());
        } catch (JsonProcessingException e) {
            logger.error("Error serializing comment event", e);
            throw new RuntimeException("Failed to publish comment event", e);
        }
    }
}
