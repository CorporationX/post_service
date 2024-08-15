package faang.school.postservice.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Data
@Slf4j
public class RedisMessagePublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

    public void publishViewEvent(Post post, long id) {
        try {
            PostViewEvent postViewEvent = new PostViewEvent();
            postViewEvent.setUserId(post.getAuthorId());
            postViewEvent.setPostId(post.getId());
            postViewEvent.setAuthorId(id);
            postViewEvent.setViewedAt(LocalDateTime.now());
            String message = objectMapper.writeValueAsString(postViewEvent);
            this.publish(message);
        } catch (JsonProcessingException e) {
            log.warn("Failure occurred withing converting PostViewEvent with id {} to String", post.getId(), e);
        }
    }
}
