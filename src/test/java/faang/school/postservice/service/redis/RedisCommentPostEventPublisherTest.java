package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.CommentEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisCommentPostEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic commentTopic;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private CommentEventPublisher publisher;


    @Test
    void testPublishObject() throws JsonProcessingException {
        CommentEventDto commentEvent = CommentEventDto.builder().build();
        publisher.publish(commentEvent);
        verify(objectMapper).writeValueAsString(commentEvent);
    }

    @Test
    void testPublishMessage() {
        String message = "message";
        String topic = "topic";
        when(commentTopic.getTopic()).thenReturn(topic);
        publisher.publish(message);
        verify(redisTemplate).convertAndSend(topic, message);
    }
}