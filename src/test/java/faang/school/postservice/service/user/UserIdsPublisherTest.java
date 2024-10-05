package faang.school.postservice.service.user;

import faang.school.postservice.publisher.UserIdsPublisher;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserIdsPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private UserIdsPublisher userIdsPublisher;

    @Test
    public void testPublish() {
        // Given
        Object message = List.of(1L, 2L, 3L);
        when(channelTopic.getTopic()).thenReturn("ban-commenters-by-id");

        // When
        userIdsPublisher.publish(message);

        // Then
        verify(redisTemplate, times(1)).convertAndSend(channelTopic.getTopic(), message);
    }
}
