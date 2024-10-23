package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.event.PostViewEventDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostViewEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    public void publish(Post post, Long viewerId) {
        Long authorId = getAuthorOrProjectId(post);
        PostViewEventDto event = new PostViewEventDto(
                post.getId(),
                authorId,
                viewerId,
                LocalDateTime.now()
        );

        try {
            String eventAsJson = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(redisProperties.getPostViewChannel(), eventAsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize PostViewEvent", e);
        }
    }

    private Long getAuthorOrProjectId(Post post) {
        return post.getAuthorId() != null ? post.getAuthorId() : post.getProjectId();
    }
}