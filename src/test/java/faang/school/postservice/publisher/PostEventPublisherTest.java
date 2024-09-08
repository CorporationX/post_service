package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PostEventPublisher publisher;
    private PostEvent postEvent;
    String message = "test message";

    @BeforeEach
    public void setUp() {
        postEvent = PostEvent.builder()
                .authorId(1L)
                .postId(2L)
                .build();
    }

    @Test
    @DisplayName("writeValueAsString")
    public void testWriteValueAsString() throws Exception {
        when(objectMapper.writeValueAsString(postEvent)).thenReturn(message);

        publisher.publish(postEvent);

        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), message);
        verify(objectMapper).writeValueAsString(postEvent);
    }

    @Test
    @DisplayName("getTopic")
    public void testGetTopic() throws Exception {
        when(objectMapper.writeValueAsString(Mockito.any(PostEvent.class)))
                .thenReturn(message);
        when(channelTopic.getTopic())
                .thenReturn("test-topic");

        publisher.publish(postEvent);

        verify(objectMapper, times(1))
                .writeValueAsString(Mockito.any(PostEvent.class));
        verify(redisTemplate, times(1))
                .convertAndSend("test-topic", message);
    }

    @Test
    @DisplayName("convertAndSend")
    public void testConvertAndSend() throws Exception {
        when(objectMapper.writeValueAsString(Mockito.any(PostEvent.class)))
                .thenReturn(message);
        when(channelTopic.getTopic())
                .thenReturn("test-topic");
        when(redisTemplate.convertAndSend(anyString(), anyString()))
                .thenReturn(anyLong());

        publisher.publish(postEvent);

        verify(objectMapper, times(1))
                .writeValueAsString(Mockito.any(PostEvent.class));
        verify(redisTemplate, times(1))
                .convertAndSend("test-topic", message);
        verify(redisTemplate, times(1))
                .convertAndSend(anyString(), anyString());
    }
}
