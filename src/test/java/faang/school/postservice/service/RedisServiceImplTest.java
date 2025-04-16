package faang.school.postservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RedisServiceImplTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic channelTopic;
    @InjectMocks
    private RedisServiceImpl redisService;

    @Test
    public void pushToRedisUsersForBanTest() {
        redisService.pushToRedisUsersForBan(1L);

        verify(redisTemplate, times(1))
                .convertAndSend(channelTopic.getTopic(), 1L);
    }
}
