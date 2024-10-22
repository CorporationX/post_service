package faang.school.postservice.service.publisher;

import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.publisher.post.PostViewEventPublisher;
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
class PostViewEventPublisherTest {

    @InjectMocks
    private PostViewEventPublisher postViewEventPublisher;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    private static final String POST_VIEW_EVENT_TOPIC = "PostViewEventTopic";

    @Test
    @DisplayName("Успешная отправка message")
    public void whenPublishEventShouldSuccess() {
        PostViewEvent event = PostViewEvent.builder().build();
        when(channelTopic.getTopic()).thenReturn(POST_VIEW_EVENT_TOPIC);

        postViewEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(POST_VIEW_EVENT_TOPIC, event);
    }
}