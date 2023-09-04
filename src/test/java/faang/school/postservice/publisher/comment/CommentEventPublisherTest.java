package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.publisher.CommentEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private final String topic = "comments-channel";

    private CommentEventPublisher commentEventPublisher;

    @BeforeEach
    void setUp() {
        commentEventPublisher = new CommentEventPublisher(redisTemplate, objectMapper, topic);
    }

    @Test
    void testPublish() throws JsonProcessingException {
        CommentEventDto commentEventDto = CommentEventDto.builder()
                .idComment(1)
                .contentComment("content")
                .authorIdComment(1)
                .postId(1L)
                .postAuthorId(1L)
                .build();

        when(objectMapper.writeValueAsString(commentEventDto)).thenReturn("JSON_STRING");

        commentEventPublisher.publish(commentEventDto);

        verify(redisTemplate).convertAndSend(topic, "JSON_STRING");
    }
}