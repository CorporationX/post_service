package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private ChannelTopic channelTopic = ChannelTopic.of("channelTopic");
    private CommentEventPublisher commentEventPublisher;

    @BeforeEach
    public void setUp() {
        commentEventPublisher = new CommentEventPublisher(redisTemplate, objectMapper, channelTopic);
    }

    @Test
    void testPublish() {
        CommentEventDto commentEventDto = CommentEventDto.builder().build();

        try {
            when(objectMapper.writeValueAsString(commentEventDto)).thenReturn("JSON_STRING");
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        commentEventPublisher.sendEvent(commentEventDto);

        verify(redisTemplate).convertAndSend(String.valueOf(channelTopic), "JSON_STRING");
    }
}
