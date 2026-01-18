package faang.school.postservice.service.redis;

import faang.school.postservice.cache.PostCacheRepositoryImpl;
import faang.school.postservice.cache.UserCacheRepositoryImpl;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.FeedRedisProperties;
import faang.school.postservice.mapper.feed.FeedPostMapper;
import faang.school.postservice.repository.FeedDbRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.FeedService;
import faang.school.postservice.service.FeedServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
@Testcontainers
@Import(FeedServiceImpl.class)
@EnableConfigurationProperties(FeedRedisProperties.class)
class FeedServiceIntegrationTest {

    @Container
    static final GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));

        // Must match your @ConfigurationProperties prefix
        registry.add("app.feed.redis.max-size", () -> 500);
        registry.add("app.feed.redis.key-prefix", () -> "feed:v1:");
    }

    // ---- MISSING BEANS for FeedServiceImpl (DataRedisTest doesn't create them) ----
    @MockBean private FeedDbRepository feedDbRepository;
    @MockBean private UserServiceClient userServiceClient;
    @MockBean private PostRepository postRepository;
    @MockBean private PostCacheRepositoryImpl postCacheRepository;
    @MockBean private UserCacheRepositoryImpl userCacheRepository;
    @MockBean private FeedPostMapper feedPostMapper;

    @Autowired FeedService feedService;
    @Autowired StringRedisTemplate redis;
    @Autowired FeedRedisProperties feedProps;

    private int originalMaxSize;

    @BeforeEach
    void cleanRedis() {
        redis.getConnectionFactory().getConnection().serverCommands().flushAll();
        originalMaxSize = feedProps.getMaxSize();
    }

    @AfterEach
    void restoreProps() {
        // avoid test-order side effects
        feedProps.setMaxSize(originalMaxSize);
    }

    @Test
    @DisplayName("addPostToFeed: adds postIds and getLatestPosts returns newest first")
    void addPostToFeed_returnsNewestFirst() {
        long followerId = 101L;

        feedService.addPostToFeed(followerId, 1L, Instant.parse("2025-12-26T10:00:00Z"));
        feedService.addPostToFeed(followerId, 2L, Instant.parse("2025-12-26T10:00:01Z"));
        feedService.addPostToFeed(followerId, 3L, Instant.parse("2025-12-26T10:00:02Z"));

        List<Long> latest = feedService.getLatestPosts(followerId, 10);

        assertThat(latest).containsExactly(3L, 2L, 1L);
    }

    @Test
    @DisplayName("addPostToFeed: idempotent (same postId twice doesn't duplicate in ZSET)")
    void addPostToFeed_isIdempotent() {
        long followerId = 202L;
        Instant ts = Instant.parse("2025-12-26T10:00:00Z");

        feedService.addPostToFeed(followerId, 42L, ts);
        feedService.addPostToFeed(followerId, 42L, ts);

        String key = feedProps.getKeyPrefix() + followerId;
        Long size = redis.opsForZSet().zCard(key);

        assertThat(size).isEqualTo(1L);
        assertThat(feedService.getLatestPosts(followerId, 10)).containsExactly(42L);
    }

    @Test
    @DisplayName("addPostToFeed: trims to maxSize (keeps newest posts, drops oldest)")
    void addPostToFeed_trimsToMaxSize() {
        long followerId = 303L;

        feedProps.setMaxSize(3);

        feedService.addPostToFeed(followerId, 1L, Instant.parse("2025-12-26T10:00:00Z"));
        feedService.addPostToFeed(followerId, 2L, Instant.parse("2025-12-26T10:00:01Z"));
        feedService.addPostToFeed(followerId, 3L, Instant.parse("2025-12-26T10:00:02Z"));
        feedService.addPostToFeed(followerId, 4L, Instant.parse("2025-12-26T10:00:03Z"));
        feedService.addPostToFeed(followerId, 5L, Instant.parse("2025-12-26T10:00:04Z"));

        String key = feedProps.getKeyPrefix() + followerId;
        Long size = redis.opsForZSet().zCard(key);

        assertThat(size).isEqualTo(3L);
        assertThat(feedService.getLatestPosts(followerId, 10)).containsExactly(5L, 4L, 3L);
    }
}
