package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.service.post.RedisCache;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
public class RedisCacheTest {

    private ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ZSetOperations<String, String> zSetOperations;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RedisCache redisCache;

    private int ttl = 1800;
    private int maxSizeFeed = 100;
    private int maxSizeComment = 3;
    private String feed = "feed:";
    private String comment = "comment:";
    private String like = "like:";
    private String timestamp = "timestamp:";

    @BeforeEach
    public void setUp() {
        redisCache.setTtl(ttl);
        redisCache.setMaxSizeComment(maxSizeComment);
        redisCache.setMaxSizeFeed(maxSizeFeed);
        redisCache.setFeed(feed);
        redisCache.setComment(comment);
        redisCache.setLike(like);
        redisCache.setTimestamp(timestamp);
    }

    @Test
    public void testSaveToCache() throws JsonProcessingException {
        String pattern = "pattern";
        Long key = 1L;
        Object value = new Object();
        String jsonValue = "{\"key\":\"value\"}";
        Mockito.when(objectMapper.writeValueAsString(value)).thenReturn(jsonValue);
        Mockito.when(redisTemplate.execute(Mockito.any(RedisCallback.class))).thenReturn(true);
        redisCache.saveToCache(pattern, key, value);
        Mockito.verify(objectMapper, Mockito.times(1)).writeValueAsString(value);
        Mockito.verify(redisTemplate, Mockito.times(1)).execute(Mockito.any(RedisCallback.class));
    }

    @Test
    public void testSaveToCacheWithException() throws JsonProcessingException {
        String pattern = "pattern";
        Long key = 1L;
        Object value = new Object();
        Mockito.when(objectMapper.writeValueAsString(value)).thenThrow(JsonProcessingException.class);
        Assert.assertThrows(RuntimeException.class, () -> redisCache.saveToCache(pattern, key, value));
        Mockito.verify(objectMapper, Mockito.times(1)).writeValueAsString(value);
        Mockito.verify(redisTemplate, Mockito.times(0)).execute(Mockito.any(RedisCallback.class));
    }

    @Test
    public void testSaveToCacheWithVersionConflict() throws JsonProcessingException {
        String pattern = "pattern";
        Long key = 1L;
        Object value = new Object();
        String jsonValue = "{\"key\":\"value\"}";
        Mockito.when(objectMapper.writeValueAsString(value)).thenReturn(jsonValue);
        Mockito.when(redisTemplate.execute(Mockito.any(RedisCallback.class))).thenReturn(false);
        Assert.assertThrows(OptimisticLockingFailureException.class, () -> redisCache.saveToCache(pattern, key, value));
        Mockito.verify(objectMapper, Mockito.times(1)).writeValueAsString(value);
        Mockito.verify(redisTemplate, Mockito.times(1)).execute(Mockito.any(RedisCallback.class));
    }

    @Test
    public void testAddPostToUserFeed() {
        Long postId = 1L;
        Long userId = 1L;
        String key = feed + userId;
        Mockito.when(redisTemplate.execute(Mockito.any(RedisCallback.class))).thenReturn(true);
        Mockito.when(zSetOperations.size(key)).thenReturn(50L);
        redisCache.addPostToUserFeed(postId, userId);
        Mockito.verify(zSetOperations, Mockito.times(1)).size(Mockito.eq(key));
    }

    @Test
    public void testAddCommentToCache() {
        Long postId = 1L;
        String commentFeedJson = "{\"key\":\"value\"}";
        String key = comment + postId;
        Mockito.when(redisTemplate.execute(Mockito.any(RedisCallback.class))).thenReturn(true);
        Mockito.when(zSetOperations.size(key)).thenReturn(20L);
        redisCache.addCommentToCache(postId, commentFeedJson);
        Mockito.verify(zSetOperations, Mockito.times(1)).size(Mockito.eq(key));
    }

    @Test
    public void testAddLikeToCache() {
        Long postId = 1L;
        String likeJson = "{\"key\":\"value\"}";
        Mockito.when(redisTemplate.execute(Mockito.any(RedisCallback.class))).thenReturn(true);
        redisCache.addLikeToCache(postId, likeJson);
        Mockito.verify(redisTemplate, Mockito.times(1)).execute(Mockito.any(RedisCallback.class));
    }

    @Test
    public void testGetFromHSetCache() {
        String pattern = "testPattern";
        String key = "testKey";
        String value = "testValue";
        Mockito.when(redisTemplate.execute(Mockito.any(RedisCallback.class))).thenReturn(value);
        String result = redisCache.getFromHSetCache(pattern, key);
        Assertions.assertEquals(value, result);
        Mockito.verify(redisTemplate, Mockito.times(1)).execute(Mockito.any(RedisCallback.class));
    }

    @Test
    public void testGetAllZSetValues() {
        String key = "testKey";
        Set<String> values = Set.of("value1", "value2");
        Mockito.when(zSetOperations.reverseRange(key, 0, -1)).thenReturn(values);
        Set<String> result = redisCache.getAllZSetValues(key);
        Assertions.assertEquals(values, result);
        Mockito.verify(zSetOperations, Mockito.times(1)).reverseRange(key, 0, -1);
    }

    @Test
    public void testGetZSetSize() {
        String key = "testKey";
        Long expectedSize = 5L;
        Mockito.when(zSetOperations.zCard(key)).thenReturn(expectedSize);
        Long actualSize = redisCache.getZSetSize(key);
        Assertions.assertEquals(expectedSize, actualSize);
        Mockito.verify(zSetOperations, Mockito.times(1)).zCard(key);
    }

    @Test
    public void testSaveZSetOption() {
        String key = "testKey";
        long currentTime = Instant.now().getEpochSecond();
        String value = "testValue";
        String timestamp = "testTimestamp";
        Mockito.when(redisTemplate.execute(Mockito.any(RedisCallback.class))).thenReturn(true);
        redisCache.saveZSetOption(key, currentTime, value, timestamp);
        Mockito.verify(redisTemplate, Mockito.times(1)).execute(Mockito.any(RedisCallback.class));
    }

    @Test
    public void testSaveZSetOptionWithOptimisticLockException() {
        String key = "testKey";
        long currentTime = Instant.now().getEpochSecond();
        String value = "testValue";
        String timestamp = "testTimestamp";
        Mockito.when(redisTemplate.execute(Mockito.any(RedisCallback.class))).thenReturn(false);
        Assert.assertThrows(OptimisticLockingFailureException.class, () -> {redisCache.saveZSetOption(key, currentTime, value, timestamp);});
    }

    @Test
    public void testCheckLimitZSet() {
        String key = "testKey";
        int maxSize = 5;
        Long currentSize = 6L;
        Mockito.when(zSetOperations.size(key)).thenReturn(currentSize);
        Mockito.doAnswer(invocation -> null)
                .when(zSetOperations)
                .removeRange(Mockito.eq(key), Mockito.eq(0L), Mockito.eq(0L));
        redisCache.checkLimitZSet(key, maxSize);
        Mockito.verify(zSetOperations, Mockito.times(1)).size(key);
        Mockito.verify(zSetOperations, Mockito.times(1)).removeRange(key, 0, 0);
    }

}
