package faang.school.postservice.service.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.PostViewEvent;
import faang.school.postservice.messaging.publisher.postevent.PostViewEventPublisher;
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
public class postViewEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PostViewEventPublisher postViewEventPublisher;
    private PostViewEvent postViewEvent;
    String message = "test message";

    @BeforeEach
    void init() {
        LocalDateTime localDateTime = LocalDateTime.now();
        postViewEvent = PostViewEvent.builder()
                .postId(1L)
                .authorId(2L)
                .userId(1L)
                .timestamp(localDateTime)
                .build();
    }

    @Test
    @DisplayName("writeValueAsString")
    public void testWriteValueAsString() throws Exception {
        when(objectMapper.writeValueAsString(postViewEvent)).thenReturn(message);

        postViewEventPublisher.publish(postViewEvent);

        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), message);
        verify(objectMapper).writeValueAsString(postViewEvent);
    }

    @Test
    @DisplayName("getTopic")
    void testGetTopic() throws Exception {
        when(objectMapper.writeValueAsString(Mockito.any(PostViewEvent.class)))
                .thenReturn(message);
        when(channelTopic.getTopic())
                .thenReturn("test-topic");

        postViewEventPublisher.publish(postViewEvent);

        verify(objectMapper, Mockito.times(1))
                .writeValueAsString(Mockito.any(PostViewEvent.class));
        verify(redisTemplate, Mockito.times(1))
                .convertAndSend("test-topic", message);
    }

    @Test
    @DisplayName("convertAndSend")
    void testConvertAndSend() throws Exception {
        when(objectMapper.writeValueAsString(Mockito.any(PostViewEvent.class)))
                .thenReturn(message);
        when(channelTopic.getTopic())
                .thenReturn("test-topic");
        when(redisTemplate.convertAndSend(anyString(), anyString()))
                .thenReturn(anyLong());

        postViewEventPublisher.publish(postViewEvent);

        verify(objectMapper, Mockito.times(1))
                .writeValueAsString(Mockito.any(PostViewEvent.class));
        verify(redisTemplate, Mockito.times(1))
                .convertAndSend("test-topic", message);
        verify(redisTemplate, times(1))
                .convertAndSend(anyString(), anyString());
    }
}