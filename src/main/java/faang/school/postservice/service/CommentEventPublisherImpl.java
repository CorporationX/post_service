package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.events.EventsProperties;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EventPublishingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Slf4j
@Service
public class CommentEventPublisherImpl implements CommentEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final EventsProperties eventsProperties;
    private final UserServiceClient userServiceClient;

    public CommentEventPublisherImpl(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            EventsProperties eventsProperties,
            UserServiceClient userServiceClient) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.eventsProperties = eventsProperties;
        this.userServiceClient = userServiceClient;
    }

    @Override
    @Retryable(
            value = {Exception.class},
            maxAttemptsExpression = "#{@eventsProperties.retry().maxAttempts()}",
            backoff = @Backoff(delayExpression = "#{@eventsProperties.retry().delay().toMillis()}")
    )
    public void publishCommentEvent(CommentEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(eventsProperties.topics().comment(), message);

            String authorName = getUserNameSafely(event.commentAuthorId());
            log.info("Published comment event for post {} by user {} ({})",
                    event.postId(), event.commentAuthorId(), authorName);

        } catch (JsonProcessingException e) {
            log.error("Error serializing comment event: {}", event, e);
            throw new EventPublishingException("Failed to serialize comment event", e);
        } catch (Exception e) {
            log.error("Redis communication error while publishing event: {}", event, e);
            throw new EventPublishingException("Redis error while publishing comment event", e);
        }
    }

    private String getUserNameSafely(Long userId) {
        try {
            UserDto user = userServiceClient.getUser(userId);
            return user.username();
        } catch (Exception e) {
            log.debug("Failed to fetch user name for userId {}: {}", userId, e.getMessage());
            return "Unknown";
        }
    }
}
