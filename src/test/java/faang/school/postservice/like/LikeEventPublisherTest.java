package faang.school.postservice.like;

import faang.school.postservice.redis.LikeEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@ExtendWith(MockitoExtension.class)
class LikeEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    @Test
    void sendMessageToRedisTest() {
        String message = "Test message";
        String topicName = "TestTopic";

        Mockito.when(topic.getTopic()).thenReturn(topicName);

        likeEventPublisher.publish(message);

        Mockito.verify(redisTemplate, Mockito.times(1)).convertAndSend(topicName, message);
    }
}
