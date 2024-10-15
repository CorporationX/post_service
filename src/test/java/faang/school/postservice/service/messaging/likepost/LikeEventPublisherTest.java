package faang.school.postservice.service.messaging.likepost;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic likeTopic;

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    @BeforeEach
    void setUp() {
        when(likeTopic.getTopic()).thenReturn("likeTopic");
    }

    @Test
    void testPublish() throws Exception {
        LocalDateTime createdAt = LocalDateTime.now();
        LikePostEvent likePostEvent = new LikePostEvent(1L, 2L, 3L, createdAt );
            String eventJson = "{\"userId\":1,\"postId\":2,\"authorId\":3,\"createdAt\":4}";
        when(objectMapper.writeValueAsString(likePostEvent)).thenReturn(eventJson);

        likeEventPublisher.publish(likePostEvent);

        verify(redisTemplate, times(1)).convertAndSend("likeTopic", eventJson);
    }
}