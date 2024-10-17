package faang.school.postservice.publis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.mapper.PostEventMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventPublisher {
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;
    private final PostEventMapper postEventMapper;

    public void publish(Post post) {
        PostEventDto postEventDto = postEventMapper.toPostEventDto(post);

        String valueAsString;
        String postEventChannelName = redisProperties.getPostEventChannelName();
        try {
            valueAsString = objectMapper.writeValueAsString(postEventDto);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(postEventChannelName, valueAsString);
        log.info("Sending message to broker: {}", postEventDto);
    }
}