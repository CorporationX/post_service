package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.LikeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeEventPublisherImplTest {
    @InjectMocks
    private LikeEventPublisherImpl likeEventPublisherImpl;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic likeEventTopic;
    private String jsonEvent;
    private LikeEvent likeEvent;

    @BeforeEach
    public void setUp() {
        jsonEvent = "jsonEvent";
        likeEvent = LikeEvent.builder()
                .postId(1L)
                .userId(2L)
                .authorId(3L)
                .createdAt(LocalDateTime.now())
                .build();
        when(likeEventTopic.getTopic()).thenReturn("like-event-topic");
    }

    @Test
    void publishLikeEventTest() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(likeEvent)).thenReturn(jsonEvent);

        likeEventPublisherImpl.publishLikeEvent(likeEvent);

        verify(redisTemplate, times(1)).convertAndSend(likeEventTopic.getTopic(), jsonEvent);
    }

}
