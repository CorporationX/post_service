package faang.school.postservice.cache;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Disabled
class RedisManagerTest {

    @MockBean
    private JedisConnectionFactory jedisConnectionFactory;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private RedisManager redisManager;

    private long timeToLive = 24L;
    private long authorId = 123L;

    @Test
    @DisplayName("The cache manager must be configured correctly")
    void testCacheManagerConfiguration() {
        CacheManager cacheManager = redisManager.cacheManager();
        assertNotNull(cacheManager);
    }

    @Test
    @DisplayName("Should cache post author successfully")
    void testCachePostAuthor() {
        String cacheKey = "postAuthor";
        redisManager.cachePostAuthor(authorId);
        verify(redisTemplate.opsForValue(), times(1)).set(cacheKey, authorId, Duration.ofHours(timeToLive));
    }
}