package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
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

@ExtendWith(MockitoExtension.class)
public class LikeEventPublisherTest {
    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic channelTopic;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testSendEvent() throws JsonProcessingException {
        LikeEvent likeEvent = LikeEvent.builder().postId(1L).build();
        String json = objectMapper.writeValueAsString(likeEvent);

        likeEventPublisher.sendEvent(likeEvent);

        verify(redisTemplate, times(1)).convertAndSend(channelTopic.getTopic(), json);
    }
}