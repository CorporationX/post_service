package faang.school.postservice.publisher;

import faang.school.postservice.model.event.LikeEvent;
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
class LikeEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private LikeEventPublisher publisher;

    @Test
    @DisplayName("Publish Like Event Test")
    void testPublish() {
        var likeEvent = LikeEvent.builder().build();
        publisher.publish(likeEvent);
        verify(redisTemplate).convertAndSend(topic.getTopic(), likeEvent);
        verifyNoMoreInteractions(redisTemplate);
    }
}
