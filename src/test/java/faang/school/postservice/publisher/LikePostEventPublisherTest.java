package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikePostEvent;
import org.junit.jupiter.api.DisplayName;
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
public class LikePostEventPublisherTest {
    @Mock
    private RedisTemplate<String, LikePostEvent> redisTemplate;
    @Mock
    private ChannelTopic likeEventTopic;

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    private final LikePostEvent likePostEvent = new LikePostEvent();

    @Test
    @DisplayName("Публикация события лайка - успешный сценарий")
    public void givenValidLikeEvent_WhenPublish_ThenEventSentToRedis() {
        likeEventPublisher.publish(likePostEvent);

        verify(redisTemplate, times(1)).convertAndSend(
                likeEventTopic.getTopic(), likePostEvent);
    }
}
