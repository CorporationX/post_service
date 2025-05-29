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
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static faang.school.postservice.contants.ErrorMessage.ERROR_SERIALIZE_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
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
        ReflectionTestUtils.setField(redisCommentService, "redisKeyPrefix", "post:%d:comment");
        ReflectionTestUtils.setField(redisCommentService, "commentCacheLuaScript", getTestLuaScript());

        json = "{\"comment\": \"text\"}";
        redisKey = "post:1:comment";
    }

    @Test
    void testSaveComment() throws JsonProcessingException {
        RedisConnection connection = mock(RedisConnection.class);
        when(jsonUtils.toJson(event)).thenReturn(json);
        when(connection.eval(any(), any(), anyInt(), any())).thenReturn(null); // или то, что ожидается

        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            return callback.doInRedis(connection);
        });

        redisCommentService.saveComment(event);

        verify(jsonUtils).toJson(event);
        verify(redisTemplate).execute(any(RedisCallback.class));
    }

    @Test
    void testSaveCommentJsonException() throws JsonProcessingException {
        when(jsonUtils.toJson(event)).thenThrow(new RuntimeException("error") {});

        RedisCommentException ex = assertThrows(RedisCommentException.class, () ->
                redisCommentService.saveComment(event));
        assertEquals(String.format(ERROR_SERIALIZE_EVENT, event.getClass().getSimpleName()), ex.getMessage());
        verify(jsonUtils).toJson(event);
        verify(redisTemplate, never()).execute((RedisCallback<Object>) any());
    }

    private String getTestLuaScript() {
        return "local key = KEYS[1]\n" +
                "local score = tonumber(ARGV[1])\n" +
                "local value = ARGV[2]\n" +
                "local maxSize = tonumber(ARGV[3])\n" +
                "redis.call('ZADD', key, score, value)\n" +
                "local size = redis.call('ZCARD', key)\n" +
                "if size > maxSize then\n" +
                "    redis.call('ZREMRANGEBYRANK', key, 0, size - maxSize - 1)\n" +
                "end\n" +
                "return nil";
    }
}