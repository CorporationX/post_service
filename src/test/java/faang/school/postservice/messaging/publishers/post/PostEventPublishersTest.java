package faang.school.postservice.messaging.publishers.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.PostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostEventPublishersTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic channelTopic;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private PostEventPublishers postEventPublishers;
    private PostEvent postEvent;

    @BeforeEach
    void init() {
        postEvent = PostEvent.builder()
                .id(1L)
                .authorId(2L)
                .build();
    }

    @Test
    void testWriteValueAsString() throws JsonProcessingException {
        String jsonString = "{\"id\":1,\"authorId\":2}";
        String topic = "Test topic";

        when(objectMapper.writeValueAsString(any(PostEvent.class)))
                .thenReturn(jsonString);
        when(channelTopic.getTopic())
                .thenReturn(topic);
        when(redisTemplate.convertAndSend(anyString(), anyString()))
                .thenReturn(anyLong());

        postEventPublishers.publish(postEvent);

        verify(objectMapper, times(1))
                .writeValueAsString(any(PostEvent.class));
        verify(channelTopic, times(1))
                .getTopic();
        verify(redisTemplate, times(1))
                .convertAndSend(anyString(), anyString());
    }
}