package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.CommentEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherForAnalyticsTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper jsonMapper;
    private CommentEventPublisherForAnalytics commentEventPublisher;
    private String json;
    private String channel;

    @BeforeEach
    void setUp() {
        channel = "comment-event-channel";
        commentEventPublisher = new CommentEventPublisherForAnalytics(redisTemplate, jsonMapper, channel);
        json = "json";
    }

    @Test
    void publish_shouldSendEventWhenMapperSucceeds() throws JsonProcessingException {
        CommentEventDto eventDto = new CommentEventDto();

        when(jsonMapper.writeValueAsString(eventDto)).thenReturn(json);

        commentEventPublisher.publish(eventDto);

        verify(redisTemplate).convertAndSend(anyString(), eq(json));
        verify(jsonMapper).writeValueAsString(eventDto);
    }
    @Test
    void publish_shouldHandleExceptionWhenMapperFails() throws JsonProcessingException {
        CommentEventDto eventDto = new CommentEventDto();

        when(jsonMapper.writeValueAsString(eventDto)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> commentEventPublisher.publish(eventDto));

        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }
}