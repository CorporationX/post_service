package faang.school.postservice.publisher;

import faang.school.postservice.model.event.BanEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisBanMessagePublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private RedisBanMessagePublisher redisBanMessagePublisher;

    @Test
    void testPublish() {
        // Arrange
        BanEvent banEvent = new BanEvent(1L);
        when(channelTopic.getTopic()).thenReturn("user_ban");

        // Act
        redisBanMessagePublisher.publish(banEvent);

        // Assert
        verify(redisTemplate, times(1)).convertAndSend("user_ban", banEvent);
    }
}
