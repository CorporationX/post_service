package faang.school.postservice.service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> template;
    @Mock
    private ChannelTopic topic;
    @InjectMocks
    private CommentEventPublisher publisher;

    @Test
    void publish() {
        // given
        String nameChannel = "topic";
        when(topic.getTopic()).thenReturn(nameChannel);
        String jsonEvent = """ 
                {
                "id": 1,
                "authorId": 2,
                "postId": 3,
                "content": "content",
                }""";
        // when
        publisher.publish(jsonEvent);
        // then
        verify(template, times(1)).convertAndSend(nameChannel, jsonEvent);
    }
}