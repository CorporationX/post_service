package faang.school.postservice.service.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.messaging.redis.publisher.post.PostEventPublisher;
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
import java.time.LocalDateTime;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private PostEventPublisher postEventPublisher;
    private PostEvent postEvent;
    String message = "test message";

    @BeforeEach
    void init() {
        LocalDateTime localDateTime = LocalDateTime.now();
        postEvent = PostEvent.builder()
                .postId(1L)
                .authorId(2L)
                .userId(1L)
                .timestamp(localDateTime)
                .build();
    }

    @Test
    @DisplayName("writeValueAsString")
    public void testWriteValueAsString() throws Exception {
        when(objectMapper.writeValueAsString(postEvent)).thenReturn(message);

        postEventPublisher.publish(postEvent);

        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), message);
        verify(objectMapper).writeValueAsString(postEvent);
    }

    @Test
    @DisplayName("getTopic")
    void testGetTopic() throws Exception {
        when(objectMapper.writeValueAsString(Mockito.any(PostEvent.class))).thenReturn(message);
        when(channelTopic.getTopic()).thenReturn("test-topic");

        postEventPublisher.publish(postEvent);

        verify(objectMapper, Mockito.times(1))
                .writeValueAsString(Mockito.any(PostEvent.class));
        verify(redisTemplate, Mockito.times(1))
                .convertAndSend("test-topic", message);
    }

    @Test
    @DisplayName("convertAndSend")
    void testConvertAndSend() throws Exception {
        when(objectMapper.writeValueAsString(Mockito.any(PostEvent.class)))
                .thenReturn(message);
        when(channelTopic.getTopic())
                .thenReturn("test-topic");
        when(redisTemplate.convertAndSend(anyString(), anyString()))
                .thenReturn(anyLong());

        postEventPublisher.publish(postEvent);

        verify(objectMapper, Mockito.times(1))
                .writeValueAsString(Mockito.any(PostEvent.class));
        verify(redisTemplate, Mockito.times(1))
                .convertAndSend("test-topic", message);
        verify(redisTemplate, times(1))
                .convertAndSend(anyString(), anyString());
    }
}