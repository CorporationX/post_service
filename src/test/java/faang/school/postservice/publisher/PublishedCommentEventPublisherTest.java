package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublishedCommentEventPublisherTest {

    private static final String TOPIC_NAME = "TEST";

    @InjectMocks
    private PublishedCommentEventPublisher publishedCommentEventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic newCommentEventTopic;

    @Test
    @DisplayName("Should send message to redis")
    void whenGetMessageThenSendMessageToRedis() {
        CommentEventDto commentEventDto = CommentEventDto.builder().build();
        when(newCommentEventTopic.getTopic()).thenReturn(TOPIC_NAME);

        publishedCommentEventPublisher.publish(commentEventDto);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

        verify(redisTemplate).convertAndSend(topicCaptor.capture(), messageCaptor.capture());

        assertEquals(TOPIC_NAME, topicCaptor.getValue());
        assertSame(commentEventDto, messageCaptor.getValue());
    }
}