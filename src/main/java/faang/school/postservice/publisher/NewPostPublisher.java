package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class NewPostPublisher implements MessagePublisher<PostDto> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper;

    public NewPostPublisher(RedisTemplate<String, Object> redisTemplate,
                            @Qualifier("newPostPublisher") ChannelTopic topic, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(PostDto message) {
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(topic.getTopic(), json);
    }
}
