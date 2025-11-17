package faang.school.postservice.publisher;

import faang.school.postservice.event.LikeEvent;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LikeEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private LikeEventPublisher publisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "channelName", "like_channel");
    }

    @Test
    void testPublish_sendsMessageToRedis() {

        LikeEvent event = new LikeEvent(1L, 2L, 3L, LocalDateTime.now());

        publisher.publish(event);

        Mockito.verify(redisTemplate)
                .convertAndSend("like_channel", event);
    }
}
