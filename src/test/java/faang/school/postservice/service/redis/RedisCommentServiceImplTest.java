package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.exception.RedisCommentException;
import faang.school.postservice.utils.json.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static faang.school.postservice.contants.ErrorMessage.ERROR_SERIALIZE_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisCommentServiceImplTest {

    @InjectMocks
    private RedisCommentServiceImpl redisCommentService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ZSetOperations<String, String> zSetOperations;
    @Mock
    private JsonUtils jsonUtils;

    private CommentEvent event;
    private String json;
    private String redisKey;

    @BeforeEach
    void setUp() {
        int maxCommentCache = 3;
        event = new CommentEvent();
        event.setCommentId(3L);
        event.setAuthorId(2L);
        event.setPostId(1L);
        event.setTimestamp(LocalDateTime.now());

        redisCommentService = new RedisCommentServiceImpl(redisTemplate, jsonUtils);
        ReflectionTestUtils.setField(redisCommentService, "maxCommentCache", maxCommentCache);
        json = "{\"comment\": \"text\"}";
        redisKey = "post:1:comment";
    }

    @Test
    void testSaveComment() throws JsonProcessingException {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard(redisKey)).thenReturn(5L);
        when(jsonUtils.toJson(event)).thenReturn(json);

        redisCommentService.saveComment(event);

        verify(zSetOperations).zCard(redisKey);
        verify(zSetOperations).removeRange(redisKey, 0, 1);
        verify(redisTemplate.opsForZSet()).add(eq(redisKey), eq(json), anyDouble());
    }

    @Test
    void testSaveCommentNotRemoveRange() throws JsonProcessingException {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard(redisKey)).thenReturn(3L);
        when(jsonUtils.toJson(event)).thenReturn(json);

        redisCommentService.saveComment(event);

        verify(zSetOperations).zCard(redisKey);
        verify(zSetOperations, never()).removeRange(redisKey, 0, 1);
        verify(redisTemplate.opsForZSet()).add(eq(redisKey), eq(json), anyDouble());
    }

    @Test
    void testSaveCommentJsonException() throws JsonProcessingException {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard(redisKey)).thenReturn(2L);
        when(jsonUtils.toJson(event)).thenThrow(new RuntimeException("error") {});

        RedisCommentException ex = assertThrows(RedisCommentException.class, () ->
                redisCommentService.saveComment(event));
        assertEquals(String.format(ERROR_SERIALIZE_EVENT, event.getClass().getSimpleName()), ex.getMessage());
    }
}