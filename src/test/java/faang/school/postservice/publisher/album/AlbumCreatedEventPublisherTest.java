package faang.school.postservice.publisher.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.AlbumCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumCreatedEventPublisherTest {

    @InjectMocks
    private AlbumCreatedEventPublisher publisher;

    @Value("${spring.data.redis.channels.album_created_channel.name}")
    private String albumCreatedTopic;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    void testPublish() throws JsonProcessingException {
        String json = "Test";
        AlbumCreatedEvent event = AlbumCreatedEvent.builder().build();
        when(objectMapper.writeValueAsString(event)).thenReturn(json);
        publisher.publish(event);
        verify(redisTemplate, times(1)).convertAndSend(albumCreatedTopic, json);
    }

    @Test
    public void testOnMessageIfThrowsIOException() throws IOException {
        AlbumCreatedEvent event = AlbumCreatedEvent.builder().build();
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publish(event));

        assertEquals("Failed to serialize album creating event: " + event, exception.getMessage());
    }

}