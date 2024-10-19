package faang.school.postservice.publisher;

import faang.school.postservice.model.event.PostViewEvent;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class PostEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic postTopic;

    @InjectMocks
    private PostViewEventPublisher postEventPublisher;

    @Test
    @DisplayName("Publish Post Event Test")
    void testPublish() {
        var postEvent = PostViewEvent.builder().build();
        postEventPublisher.publish(postEvent);
        verify(redisTemplate, times(1))
                .convertAndSend(postTopic.getTopic(), postEvent);
        verifyNoMoreInteractions(redisTemplate);
    }
}