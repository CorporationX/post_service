package faang.school.postservice.publis.publisher;

import faang.school.postservice.config.redis.RedisProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private RedisProperties redisProperties;
    @InjectMocks
    private CommentEventPublisher commentEventPublisher;

    @Test
    void testPublish_Success() {
        String message = "TestMessage";
        String channelName = "test_channel";

        when(redisProperties.getCommentEventChannelName()).thenReturn(channelName);

        commentEventPublisher.publish(message);

        verify(redisTemplate, atLeastOnce()).convertAndSend(channelName, message);
    }
}