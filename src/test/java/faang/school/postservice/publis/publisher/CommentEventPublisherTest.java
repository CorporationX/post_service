package faang.school.postservice.publis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.test_data.TestDataComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private RedisProperties redisProperties;
    @InjectMocks
    private CommentEventPublisher commentEventPublisher;

    private CommentEventDto commentEventDto;

    @BeforeEach
    void setUp() {
        TestDataComment testDataComment = new TestDataComment();
        commentEventDto = testDataComment.getCommentEventDto();
    }

    @Test
    void testPublish_Success() throws JsonProcessingException {
        String channelName = "test_channel";
        String expectedJson = objectMapper.writeValueAsString(commentEventDto);

        when(redisProperties.getCommentEventChannelName()).thenReturn(channelName);

        commentEventPublisher.publish(commentEventDto);

        verify(redisTemplate, atLeastOnce()).convertAndSend(channelName, expectedJson);
    }

    @Test
    void testPublish_invalidParse_throwsRuntimeException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(commentEventDto)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> commentEventPublisher.publish(commentEventDto));
    }
}