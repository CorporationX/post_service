package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.messaging.redis.publisher.RedisMessagePublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisMessagePublisherTest {
    @InjectMocks
    private RedisMessagePublisher redisMessagePublisher;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testPublish() throws JsonProcessingException {
        String channel = "test-channel";
        String json = "test-json";

        when(objectMapper.writeValueAsString(json)).thenReturn(json);

        redisMessagePublisher.publish(channel, json);

        verify(objectMapper, times(1)).writeValueAsString(json);
        verify(redisTemplate, times(1)).convertAndSend(channel, json);
    }
}
