package faang.school.postservice.service.feed.warmup;

import faang.school.postservice.client.FollowFeignClient;
import faang.school.postservice.config.properties.cache.feed.FeedWarmupProperties;
import faang.school.postservice.dto.user.feed.CacheWarmupTask;
import faang.school.postservice.dto.user.follower.FollowersPage;
import faang.school.postservice.kafka.producer.warmup.FeedWarmupProducer;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedWarmupStarterTest {

    private static final String LOCK_KEY = "feed:heat:lock";

    @Mock
    private PostRepository postRepository;

    @Mock
    private FollowFeignClient followFeignClient;

    @Mock
    private FeedWarmupProducer feedWarmupProducer;

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Captor
    private ArgumentCaptor<CacheWarmupTask> taskCaptor;

    private FeedWarmupStarterImpl service;

    @BeforeEach
    void setUp() {
        FeedWarmupProperties props = new FeedWarmupProperties(500, 8, 24, 6,500);
        when(redis.opsForValue()).thenReturn(valueOps);
        service = new FeedWarmupStarterImpl(postRepository, followFeignClient, feedWarmupProducer, redis, props);
    }

    @Test
    @DisplayName("Fails if lock is held")
    public void startWarmupLockAlreadyHeld() {
        when(valueOps.setIfAbsent(eq(LOCK_KEY), eq("1"), any(Duration.class))).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class, service::startWarmup);
        assertThat(ex).hasMessageContaining("already running");

        verify(redis, never()).delete(LOCK_KEY);

        verifyNoInteractions(postRepository, followFeignClient, feedWarmupProducer);
    }

    @Test
    public void startWarmupNoAuthors() {
        when(valueOps.setIfAbsent(eq(LOCK_KEY), eq("1"), any(Duration.class))).thenReturn(true);
        when(postRepository.findDistinctAuthorIdsOfPublished()).thenReturn(List.of());

        service.startWarmup();

        verify(postRepository).findDistinctAuthorIdsOfPublished();
        verifyNoInteractions(followFeignClient, feedWarmupProducer);

        verify(redis).delete(LOCK_KEY);
    }

    @Test
    @DisplayName("No users after expansion → nothing to publish")
    public void startWarmupNoUsersAfterExpansion() {
        when(valueOps.setIfAbsent(eq(LOCK_KEY), eq("1"), any(Duration.class))).thenReturn(true);
        when(postRepository.findDistinctAuthorIdsOfPublished()).thenReturn(List.of(44L));
        when(followFeignClient.getFollowerIds(44L, null, 1000))
                .thenReturn(new FollowersPage(List.of(), null));

        doNothing().when(feedWarmupProducer).publishBatch(any(CacheWarmupTask.class));

        service.startWarmup();

        verify(feedWarmupProducer, times(1)).publishBatch(any(CacheWarmupTask.class));
        verify(redis).delete(LOCK_KEY);
    }
}
