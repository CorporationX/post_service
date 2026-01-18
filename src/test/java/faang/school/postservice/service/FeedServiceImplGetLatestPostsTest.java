package faang.school.postservice.service;

import faang.school.postservice.cache.PostCacheRepositoryImpl;
import faang.school.postservice.cache.UserCacheRepositoryImpl;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.FeedRedisProperties;
import faang.school.postservice.mapper.feed.FeedPostMapper;
import faang.school.postservice.repository.FeedDbRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplGetLatestPostsTest {

    private static final long USER_ID = 10L;
    private static final String KEY_PREFIX = "feed:";

    @Mock private FeedDbRepository feedDbRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private PostRepository postRepository;
    @Mock private PostCacheRepositoryImpl postCacheRepository;
    @Mock private UserCacheRepositoryImpl userCacheRepository;
    @Mock private FeedPostMapper feedPostMapper;

    @Mock private StringRedisTemplate redis;
    @Mock private FeedRedisProperties props;
    @Mock private ZSetOperations<String, String> zsetOps;

    @InjectMocks
    private FeedServiceImpl service;

    @BeforeEach
    void setUp() {
        when(props.getKeyPrefix()).thenReturn(KEY_PREFIX);
        when(redis.opsForZSet()).thenReturn(zsetOps);
    }

    @Test
    @DisplayName("getLatestPosts should return empty list when Redis returns null range")
    void getLatestPosts_shouldReturnEmpty_whenRangeNull() {
        when(zsetOps.reverseRange(eq(KEY_PREFIX + USER_ID), eq(0L), eq(9L)))
                .thenReturn(null);

        List<Long> ids = service.getLatestPosts(USER_ID, 10);

        assertNotNull(ids);
        assertTrue(ids.isEmpty());
    }

    @Test
    @DisplayName("getLatestPosts should parse Redis members to Long")
    void getLatestPosts_shouldParseIds() {
        when(zsetOps.reverseRange(eq(KEY_PREFIX + USER_ID), eq(0L), eq(2L)))
                .thenReturn(new LinkedHashSet<>(List.of("101", "102", "103")));

        List<Long> ids = service.getLatestPosts(USER_ID, 3);

        assertEquals(List.of(101L, 102L, 103L), ids);
    }
}
