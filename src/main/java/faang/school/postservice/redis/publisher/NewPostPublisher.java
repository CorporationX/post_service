package faang.school.postservice.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewPostPublisher implements MessagePublisher<PostDto> {
    @Value("${redis.channels.hashtags}")
    String hashtagsTopic;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(PostDto message) throws JsonProcessingException {
        redisTemplate.convertAndSend(hashtagsTopic, objectMapper.writeValueAsString(message));
    }
}
