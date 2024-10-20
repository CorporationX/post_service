package faang.school.postservice.publisher.ban;

import faang.school.postservice.event.ban.UserBanEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserBanMessagePublisherTest {

    private static final Long USER_ID = 123L;
    private static final String TOPIC_NAME = "user-ban-topic";

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic userBanTopic;

    @InjectMocks
    private UserBanMessagePublisher userBanMessagePublisher;

    private UserBanEvent userBanEvent;

    @BeforeEach
    void setUp() {
        userBanEvent = new UserBanEvent(USER_ID);
    }

    @Test
    @DisplayName("Should publish UserBanEvent to the correct Redis topic")
    void shouldPublishUserBanEventToRedis() {
        when(userBanTopic.getTopic()).thenReturn(TOPIC_NAME);

        userBanMessagePublisher.publish(userBanEvent);

        verify(redisTemplate).convertAndSend(TOPIC_NAME, userBanEvent);
    }
}
