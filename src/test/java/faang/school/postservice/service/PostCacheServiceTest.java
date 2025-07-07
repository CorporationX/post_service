package faang.school.postservice.service;

import faang.school.postservice.entity.CachedPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class PostCacheServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOps;

    @Mock
    private ZSetOperations<String, String> zsetOps;

    private PostCacheService postCacheService;

    private CachedPost post;

    @BeforeEach
    void setUp() {
        postCacheService = new PostCacheService(redisTemplate, stringRedisTemplate);

        setField(postCacheService, "ttl", 3600L);
        setField(postCacheService, "feedSize", 50L);
        setField(postCacheService, "globalFeedSize", 100L);

        post = CachedPost.builder()
                .id(123L)
                .authorId(1L)
                .likes(50)
                .content("Ooga Booga")
                .publishedAt(Instant.now())
                .build();
    }

    @Test
    void positiveCachePost() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(stringRedisTemplate.opsForZSet()).thenReturn(zsetOps);
        postCacheService.cachePost(post);

        verify(hashOps).put("posts:", "123", post);
        verify(redisTemplate).expire("posts:123", 3600L, TimeUnit.SECONDS);
        verify(zsetOps).add("feed:top", "123", 50.0);
        verify(zsetOps).removeRange("feed:top", 0, -101);
    }

    @Test
    void positiveAddToUserFeed() {
        when(stringRedisTemplate.opsForZSet()).thenReturn(zsetOps);
        Long userId = 42L;
        String key = "user:feed:42";

        postCacheService.addToUserFeed(post, userId);

        verify(zsetOps).add(eq(key), eq("123"), eq((double) post.getPublishedAt().toEpochMilli()));
        verify(zsetOps).removeRange(key, 0, -51);
    }

    @Test
    void positiveGetGlobalFeed() {
        when(stringRedisTemplate.opsForZSet()).thenReturn(zsetOps);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(zsetOps.reverseRange("feed:top", 0, 4)).thenReturn(Set.of("123"));
        when(hashOps.get("posts:", "123")).thenReturn(post);

        List<CachedPost> result = postCacheService.getGlobalFeed(5);

        assertThat(result).hasSize(1).containsExactly(post);
    }

    @Test
    void positiveGlobalFeedIsEmpty() {
        when(stringRedisTemplate.opsForZSet()).thenReturn(zsetOps);
        when(zsetOps.reverseRange("feed:top", 0, 4)).thenReturn(null);

        List<CachedPost> result = postCacheService.getGlobalFeed(5);

        assertThat(result).isEmpty();
    }
}
