package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.topic.CommentEventTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {

    @InjectMocks
    private CommentEventPublisher publisher;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private CommentEventTopic topic;

    @Mock
    private ObjectMapper objectMapper;

    private CommentEvent commentEvent;

    @BeforeEach
    public void init() {
        commentEvent = new CommentEvent();
    }

    @Test
    void publish_whenOk() throws JsonProcessingException {
        String json = "json";
        when(objectMapper.writeValueAsString(commentEvent)).thenReturn(json);

        publisher.publish(commentEvent);

        Mockito.verify(redisTemplate, Mockito.times(1))
                .convertAndSend(Mockito.any(), Mockito.any());
        Mockito.verify(topic, Mockito.times(1))
                .getTopic();
    }
}