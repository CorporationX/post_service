package faang.school.postservice.service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewPostPublisher implements MessagePublisher<PostDto> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic hashtagTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(PostDto message) {
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(hashtagTopic.getTopic(), json);
    }
}
