package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.CommentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {

    @InjectMocks
    CommentEventPublisher commentEventPublisher;
    @Value("${spring.data.redis.channels.comment-channel.name}")
    String commentEventTopic;
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    @Mock
    ObjectMapper objectMapper;

    @Test
    void publish_successfulSerialization() throws JsonProcessingException {
        CommentEvent event = new CommentEvent(1L, 1L, 1L, LocalDateTime.of(2024, 7, 1, 0 , 0));
        String json = "{\"Id\":\"Id\":\"Id\",\"timestamp\":1234567890}";

        when(objectMapper.writeValueAsString(event)).thenReturn(json);

        commentEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(commentEventTopic, json);
    }


}