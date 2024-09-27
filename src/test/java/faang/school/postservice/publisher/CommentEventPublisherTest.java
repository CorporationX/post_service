package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.CommentEvent;
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
    @InjectMocks
    private CommentEventPublisher commentEventPublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic channelTopic;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    void givenValidCommentEventWhenPublishThenEventIsSerializedAndSentToRedis() throws JsonProcessingException {
        // given - precondition
        var event = CommentEvent.builder()
                .authorId(1L)
                .commentId(123L)
                .postId(456L)
                .build();
        var message = "{\"postId\":456,\"authorId\":1,\"commentId\":123}";
        var topicName = "comment_channel";

        when(objectMapper.writeValueAsString(event))
                .thenReturn(message);
        when(channelTopic.getTopic())
                .thenReturn(topicName);

        // when - action
        commentEventPublisher.publish(event);

        // then - verify the output
        verify(objectMapper, times(1))
                .writeValueAsString(event);
        verify(redisTemplate, times(1))
                .convertAndSend(topicName, message);
    }
}