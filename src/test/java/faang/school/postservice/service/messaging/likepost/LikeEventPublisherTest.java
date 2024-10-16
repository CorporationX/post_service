package faang.school.postservice.service.messaging.likepost;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.model.enums.LikePostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

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
    private ChannelTopic likeEventTopic;

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    @BeforeEach
    void setUp() {
        when(likeEventTopic.getTopic()).thenReturn("likeEventTopic");
    }

    @Test
    void testPublish() throws Exception {
        LikePostEvent likePostEvent = new LikePostEvent(1L, 2L, 3L);
        String eventJson = "{\"userId\":1,\"postId\":2,\"authorId\":3}";
        when(objectMapper.writeValueAsString(likePostEvent)).thenReturn(eventJson);

        likeEventPublisher.publish(likePostEvent);

        verify(redisTemplate, times(1)).convertAndSend("likeEventTopic", eventJson);
    }
}