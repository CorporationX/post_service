package faang.school.postservice.publisher;

import faang.school.postservice.dto.redis.CommentEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
@ExtendWith(MockitoExtension.class)
class CommentPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.comment_channel}")
    private String commentChannelName;
    @InjectMocks
    private CommentPublisher commentPublisher;
    @Test
    public void TestPublish() {
        CommentEventDto expectedEvent = CommentEventDto
                .builder()
                .receivedAt(LocalDateTime.now())
                .build();

        redisTemplate.convertAndSend(commentChannelName, expectedEvent);

        Mockito.verify(redisTemplate).convertAndSend(commentChannelName, expectedEvent);
    }
}