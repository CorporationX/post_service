package faang.school.postservice.publishers.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.event.redis.like.LikeEvent;
import faang.school.postservice.messaging.publisher.like.LikeEventPublisher;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LikePublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic channelTopic;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private LikeEventPublisher likeEventPublisher;
    private LikeEvent likeEvent;
    String jsonString;

    @BeforeEach
    void init() {
        likeEvent = LikeEvent.builder()
                .authorId(1L)
                .postId(2L)
                .likeId(3L)
                .build();

        jsonString = "{\"authorId\":1L,\"postId\":2L,\"likeId\":3L\"}";
    }

    @Test
    void testPublishWriteValueAsString() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(Mockito.any(LikeEvent.class)))
                .thenReturn(jsonString);

        likeEventPublisher.publish(likeEvent);
    }

    @Test
    void testPublishConvertAndSend() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(Mockito.any(LikeEvent.class)))
                .thenReturn(jsonString);
        when(channelTopic.getTopic())
                .thenReturn("testTopic");

        likeEventPublisher.publish(likeEvent);

        Mockito.verify(redisTemplate).convertAndSend("testTopic", jsonString);
    }
}