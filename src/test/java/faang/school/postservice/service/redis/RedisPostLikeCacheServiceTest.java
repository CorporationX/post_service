package faang.school.postservice.service.redis;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeDataFetcher;
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
class RedisPostLikeCacheServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private LikeDataFetcher likeService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private RedisPostLikeCacheService redisPostLikeCacheService;

    private final Long postId = 1L;
    private final Long userId = 100L;
    private final long likesCacheTtlMinutes = 60;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(redisPostLikeCacheService, "likesCacheTtl", likesCacheTtlMinutes);
        ReflectionTestUtils.setField(redisPostLikeCacheService, "entityName", "post");
    }

    @Test
    void testKeyPrefix() {
        String prefix = ReflectionTestUtils
                .invokeMethod(redisPostLikeCacheService, "keyPrefix");
        assertEquals("post:likes:", prefix);
    }

    @Test
    void testZsetKey() {
        String expectedKey = "post:likes:" + postId;
        assertEquals(expectedKey, redisPostLikeCacheService.zsetKey(postId));
    }

    @Test
    void testZsetKeyNullPostId() {
        String expectedKey = "post:likes:null_post_id";
        assertEquals(expectedKey, redisPostLikeCacheService.zsetKey(null));
    }

    @Test
    void testCntKey() {
        String expectedKey = "post:likes:cnt:" + postId;
        assertEquals(expectedKey, redisPostLikeCacheService.cntKey(postId));
    }

    @Test
    void testCntKeyNullPostId() {
        String expectedKey = "post:likes:cnt:null_post_id";
        assertEquals(expectedKey, redisPostLikeCacheService.cntKey(null));
    }

    @Test
    void testFetchTotalLikesCountFromDb() {
        long expectedCount = 10L;
        when(likeService.getLikesCountByPostId(postId))
                .thenReturn(expectedCount);

        long actualCount = redisPostLikeCacheService.fetchTotalLikesCountFromDb(postId);

        assertEquals(expectedCount, actualCount);
        verify(likeService).getLikesCountByPostId(postId);
    }

    @Test
    void testFetchLikesPageFromDb() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LikeDto> expectedPage = new PageImpl<>(Collections.emptyList());
        when(likeService.getLikesPageByPostId(postId, pageable))
                .thenReturn(expectedPage);

        Page<LikeDto> actualPage = redisPostLikeCacheService.fetchLikesPageFromDb(postId, pageable);

        assertEquals(expectedPage, actualPage);
        verify(likeService).getLikesPageByPostId(postId, pageable);
    }

    @Test
    void testGetTotalLikesCountCacheHit() {
        String countKey = "post:likes:cnt:" + postId;
        String cachedCount = "5";
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(valueOperations.get(countKey))
                .thenReturn(cachedCount);

        long totalLikes = redisPostLikeCacheService.getTotalLikesCount(postId);

        assertEquals(5L, totalLikes);
        verify(valueOperations).get(countKey);
        verify(likeService, never()).getLikesCountByPostId(anyLong()); // Should not call DB
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void testGetTotalLikesCountCacheMiss() {
        String countKey = "post:likes:cnt:" + postId;
        long dbCount = 15L;
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(valueOperations.get(countKey))
                .thenReturn(null);
        when(likeService.getLikesCountByPostId(postId))
                .thenReturn(dbCount);

        long totalLikes = redisPostLikeCacheService.getTotalLikesCount(postId);

        assertEquals(dbCount, totalLikes);
        verify(valueOperations).get(countKey);
        verify(likeService).getLikesCountByPostId(postId);
        verify(valueOperations).set(countKey, String.valueOf(dbCount), Duration.ofMinutes(likesCacheTtlMinutes));
    }

    @Test
    void testGetTotalLikesCountCacheInvalidFormat() {
        String countKey = "post:likes:cnt:" + postId;
        long dbCount = 25L;
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(valueOperations.get(countKey))
                .thenReturn("not_a_number");
        when(likeService.getLikesCountByPostId(postId))
                .thenReturn(dbCount);

        long totalLikes = redisPostLikeCacheService.getTotalLikesCount(postId);

        assertEquals(dbCount, totalLikes);
        verify(valueOperations).get(countKey);
        verify(likeService).getLikesCountByPostId(postId);
        verify(valueOperations).set(countKey, String.valueOf(dbCount), Duration.ofMinutes(likesCacheTtlMinutes));
    }
    @Test
    void testGetTotalLikesCount_NullEntityId() {
        long totalLikes = redisPostLikeCacheService.getTotalLikesCount(null);
        assertEquals(0L, totalLikes);
        verify(valueOperations, never()).get(any());
        verify(likeService, never()).getLikesCountByPostId(any());
    }


    @Test
    void testAddLikeToCacheNewLikeCountKeyExists() {
        String zsetKey = "post:likes:" + postId;
        String countKey = "post:likes:cnt:" + postId;
        long timestamp = System.currentTimeMillis();

        when(redisTemplate.opsForZSet())
                .thenReturn(zSetOperations);
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(zSetOperations.add(zsetKey, String.valueOf(userId), (double) timestamp))
                .thenReturn(true);
        when(redisTemplate.hasKey(countKey))
                .thenReturn(true);

        redisPostLikeCacheService.addLikeToCache(postId, userId, timestamp);

        verify(zSetOperations).add(zsetKey, String.valueOf(userId), (double) timestamp);
        verify(redisTemplate).expire(eq(zsetKey), any(Duration.class));
        verify(valueOperations).increment(countKey);
        verify(redisTemplate).expire(eq(countKey), any(Duration.class));
        verify(likeService, never()).getLikesCountByPostId(anyLong());
    }

    @Test
    void testAddLikeToCacheNewLikeCountKeyMissing() {
        String zsetKey = "post:likes:" + postId;
        String countKey = "post:likes:cnt:" + postId;
        long timestamp = System.currentTimeMillis();
        long dbCount = 5L;

        when(redisTemplate.opsForZSet())
                .thenReturn(zSetOperations);
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(zSetOperations.add(zsetKey, String.valueOf(userId), (double) timestamp))
                .thenReturn(true);
        when(redisTemplate.hasKey(countKey))
                .thenReturn(false);
        when(valueOperations.get(countKey))
                .thenReturn(null);
        when(likeService.getLikesCountByPostId(postId))
                .thenReturn(dbCount);

        redisPostLikeCacheService.addLikeToCache(postId, userId, timestamp);

        verify(zSetOperations).add(zsetKey, String.valueOf(userId), (double) timestamp);
        verify(redisTemplate).expire(eq(zsetKey), any(Duration.class));

        verify(likeService, times(2)).getLikesCountByPostId(postId); // Once for getTotalLikesCount, once for cacheTotalLikesCount
        verify(valueOperations, times(2)).set(eq(countKey), eq(String.valueOf(dbCount)), any(Duration.class));
        verify(valueOperations, never()).increment(countKey);
    }

    @Test
    void testAddLikeToCacheExistingLike() {
        String zsetKey = "post:likes:" + postId;
        long timestamp = System.currentTimeMillis();

        when(redisTemplate.opsForZSet())
                .thenReturn(zSetOperations);
        when(zSetOperations.add(zsetKey, String.valueOf(userId), (double) timestamp))
                .thenReturn(false);

        redisPostLikeCacheService.addLikeToCache(postId, userId, timestamp);

        verify(zSetOperations).add(zsetKey, String.valueOf(userId), (double) timestamp);
        verify(redisTemplate).expire(eq(zsetKey), any(Duration.class));
        verify(redisTemplate, never()).hasKey(anyString());
        verify(valueOperations, never()).increment(anyString());
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void testAddLikeToCacheNullEntityId() {
        redisPostLikeCacheService.addLikeToCache(null, userId, System.currentTimeMillis());
        verify(zSetOperations, never()).add(any(), any(), anyDouble());
    }

    @Test
    void testAddLikeToCacheNullUserId() {
        redisPostLikeCacheService.addLikeToCache(postId, null, System.currentTimeMillis());
        verify(zSetOperations, never()).add(any(), any(), anyDouble());
    }

    @Test
    void testRemoveLikeFromCacheLikeExistsCountKeyExists() {
        String zsetKey = "post:likes:" + postId;
        String countKey = "post:likes:cnt:" + postId;

        when(redisTemplate.opsForZSet())
                .thenReturn(zSetOperations);
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(zSetOperations.remove(zsetKey, String.valueOf(userId)))
                .thenReturn(1L);
        when(redisTemplate.hasKey(countKey))
                .thenReturn(true);

        redisPostLikeCacheService.removeLikeFromCache(postId, userId);

        verify(zSetOperations).remove(zsetKey, String.valueOf(userId));
        verify(redisTemplate).expire(eq(zsetKey), any(Duration.class));
        verify(valueOperations).decrement(countKey);
        verify(redisTemplate).expire(eq(countKey), any(Duration.class));
        verify(likeService, never()).getLikesCountByPostId(anyLong());
    }

    @Test
    void testRemoveLikeFromCacheLikeExistsCountKeyMissing() {
        String zsetKey = "post:likes:" + postId;
        String countKey = "post:likes:cnt:" + postId;
        long dbCount = 7L;

        when(redisTemplate.opsForZSet())
                .thenReturn(zSetOperations);
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(zSetOperations.remove(zsetKey, String.valueOf(userId)))
                .thenReturn(1L);
        when(redisTemplate.hasKey(countKey))
                .thenReturn(false);
        when(likeService.getLikesCountByPostId(postId))
                .thenReturn(dbCount);

        redisPostLikeCacheService.removeLikeFromCache(postId, userId);

        verify(zSetOperations).remove(zsetKey, String.valueOf(userId));
        verify(redisTemplate).expire(eq(zsetKey), any(Duration.class));
        verify(valueOperations, never()).decrement(countKey);
        verify(likeService).getLikesCountByPostId(postId);
        verify(valueOperations).set(countKey, String.valueOf(dbCount), Duration.ofMinutes(likesCacheTtlMinutes));
    }


    @Test
    void testRemoveLikeFromCacheLikeNotExists() {
        String zsetKey = "post:likes:" + postId;
        when(redisTemplate.opsForZSet())
                .thenReturn(zSetOperations);
        when(zSetOperations.remove(zsetKey, String.valueOf(userId)))
                .thenReturn(0L);

        redisPostLikeCacheService.removeLikeFromCache(postId, userId);

        verify(zSetOperations).remove(zsetKey, String.valueOf(userId));
        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
        verify(redisTemplate, never()).hasKey(anyString());
        verify(valueOperations, never()).decrement(anyString());
    }

    @Test
    void testRemoveLikeFromCacheNullEntityId() {
        redisPostLikeCacheService.removeLikeFromCache(null, userId);
        verify(zSetOperations, never()).remove(anyString(), any());
    }

    @Test
    void testRemoveLikeFromCacheNullUserId() {
        redisPostLikeCacheService.removeLikeFromCache(postId, null);
        verify(zSetOperations, never()).remove(anyString(), any());
    }


    @Test
    void testGetUserIdsPageCacheHit() {
        String zsetKey = "post:likes:" + postId;
        int offset = 0;
        int limit = 10;
        Set<String> userIdsFromRedis = Set.of("101", "102", "103");
        when(redisTemplate.opsForZSet())
                .thenReturn(zSetOperations);
        when(zSetOperations.reverseRange(zsetKey, offset, (long) offset + limit - 1))
                .thenReturn(userIdsFromRedis);

        List<Long> result = redisPostLikeCacheService.getUserIdsPage(postId, offset, limit);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of(101L, 102L, 103L)));
        verify(zSetOperations).reverseRange(zsetKey, offset, (long) offset + limit - 1);
    }

    @Test
    void testGetUserIdsPageCacheMissOrEmpty() {
        String zsetKey = "post:likes:" + postId;
        int offset = 0;
        int limit = 10;
        when(redisTemplate.opsForZSet())
                .thenReturn(zSetOperations);
        when(zSetOperations.reverseRange(zsetKey, offset, (long) offset + limit - 1))
                .thenReturn(null);

        List<Long> result = redisPostLikeCacheService.getUserIdsPage(postId, offset, limit);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(zSetOperations).reverseRange(zsetKey, offset, (long) offset + limit - 1);
    }

    @Test
    void testGetUserIdsPageInvalidOffsetLimit() {
        List<Long> resultNegativeOffset = redisPostLikeCacheService.getUserIdsPage(postId, -1, 10);
        assertTrue(resultNegativeOffset.isEmpty());

        List<Long> resultZeroLimit = redisPostLikeCacheService.getUserIdsPage(postId, 0, 0);
        assertTrue(resultZeroLimit.isEmpty());

        verify(zSetOperations, never()).reverseRange(anyString(), anyLong(), anyLong());
    }

    @Test
    void testGetUserIdsPageNullEntityId() {
        List<Long> result = redisPostLikeCacheService.getUserIdsPage(null, 0, 10);
        assertTrue(result.isEmpty());
        verify(zSetOperations, never()).reverseRange(anyString(), anyLong(), anyLong());
    }


    @Test
    void testPopulateLikesCacheFromDb() {
        long totalLikesFromDb = 2L;
        LikeDto like1 = new LikeDto(1L, 101L, LocalDateTime.now().minusHours(1), null, null);
        LikeDto like2 = new LikeDto(2L, 102L, LocalDateTime.now(), null, null);
        List<LikeDto> likesWindow = List.of(like1, like2);

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        String countKey = "post:likes:cnt:" + postId;
        redisPostLikeCacheService.populateLikesCacheFromDb(postId, likesWindow, totalLikesFromDb);
        verify(valueOperations).set(eq(countKey), eq(String.valueOf(totalLikesFromDb)), any(Duration.class));
        verify(redisTemplate).executePipelined(any(RedisCallback.class));
    }


    @Test
    void testPopulateLikesCacheFromDbNullEntityId() {
        redisPostLikeCacheService.populateLikesCacheFromDb(null, Collections.emptyList(), 0L);
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
        verify(redisTemplate, never()).executePipelined(any(RedisCallback.class));
    }

    @Test
    void testTouchKeysBothKeysExist() {
        String zsetKey = "post:likes:" + postId;
        String countKey = "post:likes:cnt:" + postId;
        Duration expectedTtl = Duration.ofMinutes(likesCacheTtlMinutes);

        when(redisTemplate.hasKey(zsetKey))
                .thenReturn(true);
        when(redisTemplate.hasKey(countKey))
                .thenReturn(true);

        redisPostLikeCacheService.touchKeys(postId);

        verify(redisTemplate).expire(zsetKey, expectedTtl);
        verify(redisTemplate).expire(countKey, expectedTtl);
    }

    @Test
    void testTouchKeysZsetKeyMissing() {
        String zsetKey = "post:likes:" + postId;
        String countKey = "post:likes:cnt:" + postId;
        Duration expectedTtl = Duration.ofMinutes(likesCacheTtlMinutes);

        when(redisTemplate.hasKey(zsetKey))
                .thenReturn(false);
        when(redisTemplate.hasKey(countKey))
                .thenReturn(true);

        redisPostLikeCacheService.touchKeys(postId);

        verify(redisTemplate, never()).expire(zsetKey, expectedTtl);
        verify(redisTemplate).expire(countKey, expectedTtl);
    }

    @Test
    void testTouchKeysNullEntityId() {
        redisPostLikeCacheService.touchKeys(null);
        verify(redisTemplate, never()).hasKey(anyString());
        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
    }
}