package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ChannelTopic commentTopic;

    private CommentEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher = new CommentEventPublisher(redisTemplate, objectMapper, commentTopic);
    }

    @Test
    void testPublish() throws JsonProcessingException {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(objectMapper.writeValueAsString(commentDto)).thenReturn("json");

        eventPublisher.publish(commentDto);

        verify(redisTemplate, times(1)).convertAndSend(commentTopic.getTopic(), "json");
    }
}