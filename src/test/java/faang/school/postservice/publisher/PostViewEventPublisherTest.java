package faang.school.postservice.publisher;

import faang.school.postservice.event.PostViewEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PostViewEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private PostViewEventPublisher publisher;

    PostViewEventPublisherTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublish() {
        // Arrange
        PostViewEvent event = new PostViewEvent(1L, 2L, 3L, LocalDateTime.now());
        when(topic.getTopic()).thenReturn("postViewTopic");

        // Act
        publisher.publish(event);

        // Assert
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(redisTemplate, times(1)).convertAndSend(eq("postViewTopic"), captor.capture());
        PostViewEvent capturedEvent = (PostViewEvent) captor.getValue();

        assertEquals(event.getPostId(), capturedEvent.getPostId());
        assertEquals(event.getAuthorId(), capturedEvent.getAuthorId());
        assertEquals(event.getViewerId(), capturedEvent.getViewerId());
        assertEquals(event.getViewedAt(), capturedEvent.getViewedAt());
    }
}