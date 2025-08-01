package faang.school.postservice.publisher;

import faang.school.postservice.event.CommentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCommentEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private RedisCommentEventPublisher publisher;

    @Test
    void shouldPublishCommentEventToRedis() {
        CommentEvent event = new CommentEvent(1L, 2L, 3L, 4L, "Test comment");
        when(channelTopic.getTopic()).thenReturn("comment_channel");

        publisher.publish(event);

        verify(channelTopic).getTopic();
        verify(redisTemplate).convertAndSend("comment_channel", event);
    }
}
