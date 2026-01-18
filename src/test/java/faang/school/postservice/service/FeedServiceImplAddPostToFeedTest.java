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
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplAddPostToFeedTest {

    private static final long FOLLOWER_ID = 10L;
    private static final long POST_ID_1 = 101L;

    private static final String KEY_PREFIX = "feed:";
    private static final int MAX_SIZE = 100;

    @Mock private FeedDbRepository feedDbRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private PostRepository postRepository;
    @Mock private PostCacheRepositoryImpl postCacheRepository;
    @Mock private UserCacheRepositoryImpl userCacheRepository;
    @Mock private FeedPostMapper feedPostMapper;

    @Mock private StringRedisTemplate redis;
    @Mock private FeedRedisProperties props;

    @InjectMocks
    private FeedServiceImpl service;

    @BeforeEach
    void setUp() {
        when(props.getKeyPrefix()).thenReturn(KEY_PREFIX);
        when(props.getMaxSize()).thenReturn(MAX_SIZE);
    }

    @Test
    @DisplayName("addPostToFeed should execute Lua script and pass maxSize argument")
    void addPostToFeed_shouldExecuteLuaScript() {
        Instant occurredAt = Instant.parse("2025-01-01T10:00:00Z");

        when(redis.execute(
                any(DefaultRedisScript.class),
                eq(List.of(KEY_PREFIX + FOLLOWER_ID)),
                anyString(), anyString(), anyString()
        )).thenReturn(1L);

        service.addPostToFeed(FOLLOWER_ID, POST_ID_1, occurredAt);

        verify(redis).execute(
                any(DefaultRedisScript.class),
                eq(List.of(KEY_PREFIX + FOLLOWER_ID)),
                anyString(),
                eq(String.valueOf(POST_ID_1)),
                eq(String.valueOf(MAX_SIZE))
        );
        verifyNoMoreInteractions(redis);
    }
}
