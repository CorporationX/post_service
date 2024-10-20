package faang.school.postservice.publisher.comment;

import faang.school.postservice.dto.comment.CommentEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentEventPublisherTest {

    @InjectMocks
    private CommentEventPublisher commentEventPublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic commentEventChannel;

    @Test
    @DisplayName("When valid dto passed send it to comment channel")
    public void whenValidCommentEventDtoPassedThenSendItToCommentChannel() {
        CommentEventDto commentEventDto = CommentEventDto.builder()
                .commentId(1L)
                .build();
        commentEventPublisher.publish(commentEventDto);
        verify(redisTemplate).convertAndSend(commentEventChannel.getTopic(), commentEventDto);
    }
}
