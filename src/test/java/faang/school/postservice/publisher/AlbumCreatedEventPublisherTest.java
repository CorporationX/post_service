package faang.school.postservice.publisher;

import faang.school.postservice.model.event.AlbumCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AlbumCreatedEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> restTemplate;

    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private AlbumCreatedEventPublisher publisher;

    @Test
    @DisplayName("Publish Created Album Event Test")
    void testPublish() {
        var createdAlbumEvent = AlbumCreatedEvent.builder().build();
        publisher.publish(createdAlbumEvent);
        verify(restTemplate).convertAndSend(topic.getTopic(), createdAlbumEvent);
        verifyNoMoreInteractions(restTemplate);
    }
}