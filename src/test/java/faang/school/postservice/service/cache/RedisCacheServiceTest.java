package faang.school.postservice.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RedisCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ZSetOperations<String, String> zSetOperations;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RedisCacheService redisCacheService;

    @Value("${spring.data.redis.settings.ttl}")
    private int ttl = 3600;
    @Value("${spring.data.redis.settings.maxSizeFeed}")
    private int maxSizeFeed = 100;
    @Value("${spring.data.redis.settings.maxSizeComment}")
    private int maxSizeComment = 50;
    @Value("${spring.data.redis.directory.feed}")
    private String feed = "feed:";
    @Value("${spring.data.redis.directory.comment}")
    private String comment = "comment:";
    @Value("${spring.data.redis.directory.like}")
    private String like = "like:";
    @Value("${spring.data.redis.directory.timestamp}")
    private String timestamp = "timestamp:";

    @BeforeEach
    public void setUp() {
        redisCacheService.setTtl(ttl);
        redisCacheService.setMaxSizeFeed(maxSizeFeed);
        redisCacheService.setMaxSizeComment(maxSizeComment);
        redisCacheService.setFeed(feed);
        redisCacheService.setComment(comment);
        redisCacheService.setLike(like);
        redisCacheService.setTimestamp(timestamp);
    }

    @Test
    public void testSaveToCache() throws JsonProcessingException {
        String pattern = "testPattern";
        Long key = 1L;
        Object value = new Object();
        String jsonValue = "{\"key\":\"value\"}";
        String keyStr = key.toString();

        when(objectMapper.writeValueAsString(value)).thenReturn(jsonValue);
        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(true);

        redisCacheService.saveToCache(pattern, key, value);

        verify(objectMapper, times(1)).writeValueAsString(value);
        verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
    }

    @Test
    public void testSaveToCacheWithJsonProcessingException() throws JsonProcessingException {
        String pattern = "testPattern";
        Long key = 1L;
        Object value = new Object();

        when(objectMapper.writeValueAsString(value)).thenThrow(new JsonProcessingException("Error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            redisCacheService.saveToCache(pattern, key, value);
        });

        assertTrue(exception.getMessage().contains("Error"));
        verify(objectMapper, times(1)).writeValueAsString(value);
        verify(redisTemplate, times(0)).execute(any(RedisCallback.class));
    }

    @Test
    public void testAddPostToUserFeed() {
        Long postId = 1L;
        Long userId = 1L;
        String key = feed + userId;

        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(true);
        when(zSetOperations.size(key)).thenReturn(50L);

        redisCacheService.addPostToUserFeed(postId, userId);

        verify(zSetOperations, times(1)).size(eq(key));
    }

    @Test
    public void testAddCommentToCache() {
        Long postId = 1L;
        String commentFeedJson = "{\"key\":\"value\"}";
        String key = comment + postId;

        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(true);
        when(zSetOperations.size(key)).thenReturn(20L);

        redisCacheService.addCommentToCache(postId, commentFeedJson);

        verify(zSetOperations, times(1)).size(eq(key));
    }

    @Test
    public void testAddLikeToCache() {
        Long postId = 1L;
        String likeJson = "{\"key\":\"value\"}";

        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(true);

        redisCacheService.addLikeToCache(postId, likeJson);
    }

    @Test
    public void testGetFromHSetCache() {
        String pattern = "testPattern";
        String key = "testKey";
        String value = "testValue";

        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(value);

        String result = redisCacheService.getFromHSetCache(pattern, key);

        assertEquals(value, result);
        verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
    }

    @Test
    public void testGetAllZSetValues() {
        String key = "testKey";
        Set<String> values = Set.of("value1", "value2");

        when(zSetOperations.reverseRange(key, 0, -1)).thenReturn(values);

        Set<String> result = redisCacheService.getAllZSetValues(key);

        assertEquals(values, result);
        verify(zSetOperations, times(1)).reverseRange(key, 0, -1);
    }

    @Test
    public void testGetZSetSize() {
        String key = "testKey";
        Long size = 10L;

        when(zSetOperations.zCard(key)).thenReturn(size);

        Long result = redisCacheService.getZSetSize(key);

        assertEquals(size, result);
        verify(zSetOperations, times(1)).zCard(key);
    }

    @Test
    public void testSaveZSetOption() {
        String key = "testKey";
        long currentTime = Instant.now().getEpochSecond();
        String value = "testValue";
        String timestamp = "testTimestamp";

        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(true);

        redisCacheService.saveZSetOption(key, currentTime, value, timestamp);

        verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
    }

    @Test
    public void testCheckLimitZSet() {
        String key = "testKey";
        int maxSize = 5;
        Long currentSize = 6L;

        when(zSetOperations.size(key)).thenReturn(currentSize);

        doAnswer(invocation -> {
            return null;
        }).when(zSetOperations).removeRange(eq(key), eq(0L), eq(0L));

        redisCacheService.checkLimitZSet(key, maxSize);

        verify(zSetOperations, times(1)).size(key);
        verify(zSetOperations, times(1)).removeRange(key, 0, 0);
    }

    @Test
    public void testCheckLimitZSetWhenSizeNotExceeded() {
        String key = "testKey";
        int maxSize = 5;
        Long currentSize = 4L;

        when(zSetOperations.size(key)).thenReturn(currentSize);

        redisCacheService.checkLimitZSet(key, maxSize);

        verify(zSetOperations, times(1)).size(key);
        verify(zSetOperations, times(0)).removeRange(key, 0, 0);
    }
}