package faang.school.postservice.service.redis;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeServiceInterfaceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisCommentLikeCacheServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private LikeServiceInterfaceImpl likeService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private RedisCommentLikeCacheService redisCommentLikeCacheService;

    private final Long commentId = 1L;
    private final Long userId = 100L;
    private final long likesCacheTtlMinutes = 60;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(redisCommentLikeCacheService, "likesCacheTtl", likesCacheTtlMinutes);
    }

    @Test
    void testKeyPrefix() {
        String prefix = ReflectionTestUtils.invokeMethod(redisCommentLikeCacheService, "keyPrefix");
        assertEquals("comment:likes:", prefix);
    }

    @Test
    void testZsetKey() {
        String expectedKey = "comment:likes:" + commentId;
        assertEquals(expectedKey, redisCommentLikeCacheService.zsetKey(commentId));
    }

    @Test
    void testZsetKeyNullCommentId() {
        String expectedKey = "comment:likes:null_comment_id";
        assertEquals(expectedKey, redisCommentLikeCacheService.zsetKey(null));
    }

    @Test
    void testCntKey() {
        String expectedKey = "comment:likes:cnt:" + commentId;
        assertEquals(expectedKey, redisCommentLikeCacheService.cntKey(commentId));
    }

    @Test
    void testCntKeyNullCommentId() {
        String expectedKey = "comment:likes:cnt:null_comment_id";
        assertEquals(expectedKey, redisCommentLikeCacheService.cntKey(null));
    }

    @Test
    void testFetchTotalLikesCountFromDb() {
        long expectedCount = 10L;
        when(likeService.getLikesCountByCommentId(commentId)).thenReturn(expectedCount);

        long actualCount = redisCommentLikeCacheService.fetchTotalLikesCountFromDb(commentId);

        assertEquals(expectedCount, actualCount);
        verify(likeService).getLikesCountByCommentId(commentId);
    }

    @Test
    void testFetchTotalLikesCountFromDb_NullCommentId() {
        assertThrows(IllegalArgumentException.class,
                () -> redisCommentLikeCacheService.fetchTotalLikesCountFromDb(null));
        verify(likeService, never()).getLikesCountByCommentId(any());
    }

    @Test
    void testFetchLikesPageFromDb() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LikeDto> expectedPage = new PageImpl<>(Collections.emptyList());
        when(likeService.getLikesPageByCommentId(commentId, pageable)).thenReturn(expectedPage);

        Page<LikeDto> actualPage = redisCommentLikeCacheService.fetchLikesPageFromDb(commentId, pageable);

        assertEquals(expectedPage, actualPage);
        verify(likeService).getLikesPageByCommentId(commentId, pageable);
    }

    @Test
    void testGetTotalLikesCountCacheHit() {
        String countKey = "comment:likes:cnt:" + commentId;
        String cachedCount = "5";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(countKey)).thenReturn(cachedCount);

        long totalLikes = redisCommentLikeCacheService.getTotalLikesCount(commentId);

        assertEquals(5L, totalLikes);
        verify(valueOperations).get(countKey);
        verify(likeService, never()).getLikesCountByCommentId(anyLong());
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void testGetTotalLikesCountCacheMiss() {
        String countKey = "comment:likes:cnt:" + commentId;
        long dbCount = 15L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(countKey)).thenReturn(null);
        when(likeService.getLikesCountByCommentId(commentId)).thenReturn(dbCount);

        long totalLikes = redisCommentLikeCacheService.getTotalLikesCount(commentId);

        assertEquals(dbCount, totalLikes);
        verify(valueOperations).get(countKey);
        verify(likeService).getLikesCountByCommentId(commentId);
        verify(valueOperations).set(countKey, String.valueOf(dbCount), Duration.ofMinutes(likesCacheTtlMinutes));
    }

    @Test
    void testGetTotalLikesCountCacheInvalidFormat() {
        String countKey = "comment:likes:cnt:" + commentId;
        long dbCount = 25L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(countKey)).thenReturn("not_a_number");
        when(likeService.getLikesCountByCommentId(commentId)).thenReturn(dbCount);

        long totalLikes = redisCommentLikeCacheService.getTotalLikesCount(commentId);

        assertEquals(dbCount, totalLikes);
        verify(valueOperations).get(countKey);
        verify(likeService).getLikesCountByCommentId(commentId);
        verify(valueOperations).set(countKey, String.valueOf(dbCount), Duration.ofMinutes(likesCacheTtlMinutes));
    }

    @Test
    void testGetTotalLikesCount_NullEntityId() {
        long totalLikes = redisCommentLikeCacheService.getTotalLikesCount(null);
        assertEquals(0L, totalLikes);
        verify(valueOperations, never()).get(any());
        verify(likeService, never()).getLikesCountByCommentId(any());
    }

    @Test
    void testAddLikeToCacheNewLikeCountKeyExists() {
        String zsetKey = "comment:likes:" + commentId;
        String countKey = "comment:likes:cnt:" + commentId;
        long timestamp = System.currentTimeMillis();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(zSetOperations.add(zsetKey, String.valueOf(userId), (double) timestamp)).thenReturn(true);
        when(redisTemplate.hasKey(countKey)).thenReturn(true);

        redisCommentLikeCacheService.addLikeToCache(commentId, userId, timestamp);

        verify(zSetOperations).add(zsetKey, String.valueOf(userId), (double) timestamp);
        verify(redisTemplate).expire(eq(zsetKey), any(Duration.class));
        verify(valueOperations).increment(countKey);
        verify(redisTemplate).expire(eq(countKey), any(Duration.class));
        verify(likeService, never()).getLikesCountByCommentId(anyLong());
    }

    @Test
    void testAddLikeToCacheNewLikeCountKeyMissing() {
        String zsetKey = "comment:likes:" + commentId;
        String countKey = "comment:likes:cnt:" + commentId;
        long timestamp = System.currentTimeMillis();
        long dbCount = 5L;

        when(redisTemplate.opsForZSet())
                .thenReturn(zSetOperations);
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(zSetOperations.add(zsetKey, String.valueOf(userId), (double) timestamp)).thenReturn(true);
        when(redisTemplate.hasKey(countKey))
                .thenReturn(false);
        when(valueOperations.get(countKey))
                .thenReturn(null);
        when(likeService.getLikesCountByCommentId(commentId)).thenReturn(dbCount);

        redisCommentLikeCacheService.addLikeToCache(commentId, userId, timestamp);

        verify(zSetOperations).add(zsetKey, String.valueOf(userId), (double) timestamp);
        verify(redisTemplate).expire(eq(zsetKey), any(Duration.class));
        verify(likeService, times(2)).getLikesCountByCommentId(commentId);
        verify(valueOperations, times(2)).set(eq(countKey), eq(String.valueOf(dbCount)), any(Duration.class));
        verify(valueOperations, never()).increment(countKey);
    }

    @Test
    void testAddLikeToCacheExistingLike() {
        String zsetKey = "comment:likes:" + commentId;
        long timestamp = System.currentTimeMillis();

        when(redisTemplate.opsForZSet())
                .thenReturn(zSetOperations);
        when(zSetOperations.add(zsetKey, String.valueOf(userId), (double) timestamp)).thenReturn(false);

        redisCommentLikeCacheService.addLikeToCache(commentId, userId, timestamp);

        verify(zSetOperations).add(zsetKey, String.valueOf(userId), (double) timestamp);
        verify(redisTemplate).expire(eq(zsetKey), any(Duration.class));
        verify(redisTemplate, never()).hasKey(anyString());
        verify(valueOperations, never()).increment(anyString());
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void testAddLikeToCacheNullEntityId() {
        redisCommentLikeCacheService.addLikeToCache(null, userId, System.currentTimeMillis());
        verify(zSetOperations, never()).add(any(), any(), anyDouble());
    }

    @Test
    void testAddLikeToCacheNullUserId() {
        redisCommentLikeCacheService.addLikeToCache(commentId, null, System.currentTimeMillis());
        verify(zSetOperations, never()).add(any(), any(), anyDouble());
    }

    @Test
    void testRemoveLikeFromCacheLikeExistsCountKeyExists() {
        String zsetKey = "comment:likes:" + commentId;
        String countKey = "comment:likes:cnt:" + commentId;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(zSetOperations.remove(zsetKey, String.valueOf(userId))).thenReturn(1L);
        when(redisTemplate.hasKey(countKey)).thenReturn(true);

        redisCommentLikeCacheService.removeLikeFromCache(commentId, userId);

        verify(zSetOperations).remove(zsetKey, String.valueOf(userId));
        verify(redisTemplate).expire(eq(zsetKey), any(Duration.class));
        verify(valueOperations).decrement(countKey);
        verify(redisTemplate).expire(eq(countKey), any(Duration.class));
        verify(likeService, never()).getLikesCountByCommentId(anyLong());
    }

    @Test
    void testRemoveLikeFromCacheLikeExistsCountKeyMissing() {
        String zsetKey = "comment:likes:" + commentId;
        String countKey = "comment:likes:cnt:" + commentId;
        long dbCount = 7L;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(zSetOperations.remove(zsetKey, String.valueOf(userId))).thenReturn(1L);
        when(redisTemplate.hasKey(countKey)).thenReturn(false);
        when(likeService.getLikesCountByCommentId(commentId)).thenReturn(dbCount);

        redisCommentLikeCacheService.removeLikeFromCache(commentId, userId);

        verify(zSetOperations).remove(zsetKey, String.valueOf(userId));
        verify(redisTemplate).expire(eq(zsetKey), any(Duration.class));
        verify(valueOperations, never()).decrement(countKey);
        verify(likeService).getLikesCountByCommentId(commentId);
        verify(valueOperations).set(countKey, String.valueOf(dbCount), Duration.ofMinutes(likesCacheTtlMinutes));
    }

    @Test
    void testRemoveLikeFromCacheLikeNotExists() {
        String zsetKey = "comment:likes:" + commentId;
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.remove(zsetKey, String.valueOf(userId))).thenReturn(0L);

        redisCommentLikeCacheService.removeLikeFromCache(commentId, userId);

        verify(zSetOperations).remove(zsetKey, String.valueOf(userId));
        verify(redisTemplate, never()).expire(anyString(), any(Duration.class)); // No expire if zset was not modified effectively for this user
        verify(redisTemplate, never()).hasKey(anyString());
        verify(valueOperations, never()).decrement(anyString());
    }

    @Test
    void testRemoveLikeFromCacheNullEntityId() {
        redisCommentLikeCacheService.removeLikeFromCache(null, userId);
        verify(zSetOperations, never()).remove(anyString(), any());
    }

    @Test
    void testRemoveLikeFromCacheNullUserId() {
        redisCommentLikeCacheService.removeLikeFromCache(commentId, null);
        verify(zSetOperations, never()).remove(anyString(), any());
    }

    @Test
    void testGetUserIdsPageCacheHit() {
        String zsetKey = "comment:likes:" + commentId;
        int offset = 0;
        int limit = 10;
        Set<String> userIdsFromRedis = Set.of("101", "102", "103");
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRange(zsetKey, offset, (long) offset + limit - 1)).thenReturn(userIdsFromRedis);

        List<Long> result = redisCommentLikeCacheService.getUserIdsPage(commentId, offset, limit);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of(101L, 102L, 103L)));
        verify(zSetOperations).reverseRange(zsetKey, offset, (long) offset + limit - 1);
    }

    @Test
    void testGetUserIdsPageCacheMissOrEmpty() {
        String zsetKey = "comment:likes:" + commentId;
        int offset = 0;
        int limit = 10;
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRange(zsetKey, offset, (long) offset + limit - 1)).thenReturn(null);

        List<Long> result = redisCommentLikeCacheService.getUserIdsPage(commentId, offset, limit);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(zSetOperations).reverseRange(zsetKey, offset, (long) offset + limit - 1);
    }

    @Test
    void testGetUserIdsPageInvalidOffsetLimit() {
        List<Long> resultNegativeOffset = redisCommentLikeCacheService.getUserIdsPage(commentId, -1, 10);
        assertTrue(resultNegativeOffset.isEmpty());

        List<Long> resultZeroLimit = redisCommentLikeCacheService.getUserIdsPage(commentId, 0, 0);
        assertTrue(resultZeroLimit.isEmpty());

        verify(zSetOperations, never()).reverseRange(anyString(), anyLong(), anyLong());
    }

    @Test
    void testGetUserIdsPageNullEntityId() {
        List<Long> result = redisCommentLikeCacheService.getUserIdsPage(null, 0, 10);
        assertTrue(result.isEmpty());
        verify(zSetOperations, never()).reverseRange(anyString(), anyLong(), anyLong());
    }

    @Test
    void testPopulateLikesCacheFromDb() {
        long totalLikesFromDb = 2L;
        LikeDto like1 = new LikeDto(1L, 101L, LocalDateTime.now().minusHours(1));
        LikeDto like2 = new LikeDto(2L, 102L, LocalDateTime.now());
        List<LikeDto> likesWindow = List.of(like1, like2);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String countKey = "comment:likes:cnt:" + commentId;
        redisCommentLikeCacheService.populateLikesCacheFromDb(commentId, likesWindow, totalLikesFromDb);
        verify(valueOperations).set(eq(countKey), eq(String.valueOf(totalLikesFromDb)), any(Duration.class));
        verify(redisTemplate).executePipelined(any(RedisCallback.class));
    }

    @Test
    void testPopulateLikesCacheFromDbNullEntityId() {
        redisCommentLikeCacheService.populateLikesCacheFromDb(null, Collections.emptyList(), 0L);
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
        verify(redisTemplate, never()).executePipelined(any(RedisCallback.class));
    }

    @Test
    void testTouchKeysBothKeysExist() {
        String zsetKey = "comment:likes:" + commentId;
        String countKey = "comment:likes:cnt:" + commentId;
        Duration expectedTtl = Duration.ofMinutes(likesCacheTtlMinutes);

        when(redisTemplate.hasKey(zsetKey)).thenReturn(true);
        when(redisTemplate.hasKey(countKey)).thenReturn(true);

        redisCommentLikeCacheService.touchKeys(commentId);

        verify(redisTemplate).expire(zsetKey, expectedTtl);
        verify(redisTemplate).expire(countKey, expectedTtl);
    }

    @Test
    void testTouchKeysZsetKeyMissing() {
        String zsetKey = "comment:likes:" + commentId;
        String countKey = "comment:likes:cnt:" + commentId;
        Duration expectedTtl = Duration.ofMinutes(likesCacheTtlMinutes);

        when(redisTemplate.hasKey(zsetKey)).thenReturn(false);
        when(redisTemplate.hasKey(countKey)).thenReturn(true);

        redisCommentLikeCacheService.touchKeys(commentId);

        verify(redisTemplate, never()).expire(zsetKey, expectedTtl);
        verify(redisTemplate).expire(countKey, expectedTtl);
    }

    @Test
    void testTouchKeysCountKeyMissing() {
        String zsetKey = "comment:likes:" + commentId;
        String countKey = "comment:likes:cnt:" + commentId;
        Duration expectedTtl = Duration.ofMinutes(likesCacheTtlMinutes);

        when(redisTemplate.hasKey(zsetKey)).thenReturn(true);
        when(redisTemplate.hasKey(countKey)).thenReturn(false);

        redisCommentLikeCacheService.touchKeys(commentId);

        verify(redisTemplate).expire(zsetKey, expectedTtl);
        verify(redisTemplate, never()).expire(countKey, expectedTtl);
    }

    @Test
    void testTouchKeysBothKeysMissing() {
        String zsetKey = "comment:likes:" + commentId;
        String countKey = "comment:likes:cnt:" + commentId;
        Duration expectedTtl = Duration.ofMinutes(likesCacheTtlMinutes);

        when(redisTemplate.hasKey(zsetKey)).thenReturn(false);
        when(redisTemplate.hasKey(countKey)).thenReturn(false);

        redisCommentLikeCacheService.touchKeys(commentId);

        verify(redisTemplate, never()).expire(zsetKey, expectedTtl);
        verify(redisTemplate, never()).expire(countKey, expectedTtl);
    }

    @Test
    void testTouchKeysNullEntityId() {
        redisCommentLikeCacheService.touchKeys(null);
        verify(redisTemplate, never()).hasKey(anyString());
        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
    }
}