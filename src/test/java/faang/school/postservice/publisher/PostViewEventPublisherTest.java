package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostViewEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostViewEventPublisherTest {

    @Mock
    private RedisTemplate<String, PostViewEvent> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private PostViewEventPublisher postViewEventPublisher;

    private PostViewEvent postViewEvent;
    private String topic;

    @BeforeEach
    void setUp() {
        topic = "topic";
        LocalDateTime timestamp = LocalDateTime.of(2020, 1, 1, 1, 1);
        postViewEvent = PostViewEvent.builder()
                .postId(1L)
                .authorId(2L)
                .viewerId(1L)
                .timestamp(timestamp)
                .build();
    }

    @Test
    @DisplayName("Publish event")
    void postViewEventPublisherTest_publishEvent() {
        when(channelTopic.getTopic()).thenReturn(topic);

        postViewEventPublisher.publish(postViewEvent);

        verify(channelTopic).getTopic();
        verify(redisTemplate).convertAndSend(topic, postViewEvent);
    }

    @Test
    @DisplayName("Publish event with exception")
    void postViewEventPublisherTest_publishEventWithException() {
        String expectedMessage = "exception";
        RuntimeException exception = new RuntimeException(expectedMessage);
        when(channelTopic.getTopic()).thenReturn(topic);
        when(redisTemplate.convertAndSend(topic, postViewEvent)).thenThrow(exception);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> postViewEventPublisher.publish(postViewEvent));

        assertEquals(exception.toString(), ex.getMessage());
        verify(channelTopic, times(2)).getTopic();

    }
}