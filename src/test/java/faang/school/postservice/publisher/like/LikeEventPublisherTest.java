package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.like.LikeEventDto;
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
class LikeEventPublisherTest {

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    @Mock
    private RedisTemplate<String,Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    private static final String LIKE_POST_EVENT_TOPIC = "like_event_channel";

    @Test
    @DisplayName("Успешная отправка message")
    public void whenPublishEventShouldSuccess() {
        LikeEventDto event = LikeEventDto.builder().build();
        when(channelTopic.getTopic()).thenReturn(LIKE_POST_EVENT_TOPIC);

        likeEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(LIKE_POST_EVENT_TOPIC, event);
    }
}