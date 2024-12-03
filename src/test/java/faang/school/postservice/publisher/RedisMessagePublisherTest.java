package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.UserIdDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisMessagePublisherTest {
    private static final Long AUTHOR_ID = 1L;
    private static final String TOPIC = "name";

    private UserIdDto user;

    @BeforeEach
    void setUp() {
        user = new UserIdDto();
        user.setId(1L);
    }

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RedisMessagePublisher redisMessagePublisher;

    @Test
    void testPublish() throws JsonProcessingException {
        String json = "{\"authorId\":1}";

        when(channelTopic.getTopic()).thenReturn(TOPIC);
        when(objectMapper.writeValueAsString(user)).thenReturn(json);

        redisMessagePublisher.publish(AUTHOR_ID);

        verify(redisTemplate, times(1)).convertAndSend(TOPIC, json);
    }

    @Test
    void testPublishWithJsonProcessingException() throws JsonProcessingException{
        when(objectMapper.writeValueAsString(user)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class , () -> redisMessagePublisher.publish(AUTHOR_ID));
        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }
}
