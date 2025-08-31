package faang.school.postservice.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CommentEventPublisherImpl publisher;

    @Test
    @DisplayName("Должен опубликовать событие комментария")
    void shouldPublishCommentEvent() throws Exception {
        CommentEvent event = new CommentEvent(1L, 2L, 4L, 3L, "Test comment", LocalDateTime.now());
        String serializedEvent = "{\"commentId\":1}";

        when(objectMapper.writeValueAsString(event)).thenReturn(serializedEvent);

        publisher.publishCommentEvent(event);

        verify(redisTemplate).convertAndSend(eq("comment.events"), eq(serializedEvent));
    }

    @Test
    @DisplayName("Должен обработать ошибку сериализации")
    void shouldHandleSerializationError() throws Exception {
        CommentEvent event = new CommentEvent(1L, 2L, 4L, 3L, "Test comment", LocalDateTime.now());

        when(objectMapper.writeValueAsString(event))
                .thenThrow(new RuntimeException("Serialization error"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            publisher.publishCommentEvent(event);
        });

        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }
}
