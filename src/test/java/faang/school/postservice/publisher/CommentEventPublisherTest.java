package faang.school.postservice.publisher;

import faang.school.postservice.event.CommentEvent;
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
class CommentEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private CommentEventPublisher publisher;

    @Test
    @DisplayName("Publish Comment Event Test")
    void testPublish() {
        var commentEvent = CommentEvent.builder().build();
        publisher.publish(commentEvent);
        verify(redisTemplate).convertAndSend(topic.getTopic(), commentEvent);
        verifyNoMoreInteractions(redisTemplate);
    }
}