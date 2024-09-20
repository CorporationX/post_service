package faang.school.postservice.repository.cache;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class PostCacheRepository {

    private static final String CACHE_PREFIX = "post:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${cache.post-ttl-seconds}")
    private long timeToLive;

    public void save(long postId, Post post) {
        String key = CACHE_PREFIX + postId;
        redisTemplate.opsForValue()
                .set(key, post, timeToLive, TimeUnit.SECONDS);
    }

    public Optional<Post> getPost(long postId) {
        String key = CACHE_PREFIX + postId;
        return Optional.ofNullable((Post) redisTemplate.opsForValue().get(key));
    }
}
