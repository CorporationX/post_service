package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.comment_event_channel}")
    private String commentEventChannelName;
    @InjectMocks
    private CommentEventPublisher commentEventPublisher;

    @Test
    public void testPublish() throws Exception {
        CommentEventDto expectedEvent = CommentEventDto.builder().receivedAt(LocalDateTime.now()).build();

        redisTemplate.convertAndSend(commentEventChannelName, expectedEvent);

        verify(redisTemplate).convertAndSend(commentEventChannelName, expectedEvent);
    }
}