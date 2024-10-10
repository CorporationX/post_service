package faang.school.postservice.publis.publisher;

import faang.school.postservice.config.redis.RedisProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserBanMessagePublisherTest {
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private RedisProperties redisProperties;
    @InjectMocks
    private UserBanMessagePublisher userBanMessagePublisher;

    @Test
    void testPublish() {
        String message = "[1, 2, 3]";
        String channelName = "user_ban";

        when(redisProperties.getUserBanChannelName()).thenReturn(channelName);

        userBanMessagePublisher.publish(message);

        verify(redisTemplate).convertAndSend(channelName, message);
    }
}
