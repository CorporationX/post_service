package faang.school.postservice.publicher;


import faang.school.postservice.dto.notification.CommentEvent;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.CommentEventPublisher;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic calculations_channelTopic;

    @Mock
    private CommentEventMapper commentEventMapper;

    @InjectMocks
    private CommentEventPublisher commentEventPublisher;

    @Test
    @DisplayName("Проверка публикации события в Redis")
    void testPublish() {
        Comment comment = new Comment();
        CommentEvent commentEvent = new CommentEvent();
        when(calculations_channelTopic.getTopic()).thenReturn("test-topic");
        when(commentEventMapper.toEvent(comment)).thenReturn(commentEvent);

        commentEventPublisher.publish(comment);

        verify(redisTemplate, times(1)).convertAndSend("test-topic", commentEvent);
        verify(commentEventMapper, times(1)).toEvent(comment);
    }
}