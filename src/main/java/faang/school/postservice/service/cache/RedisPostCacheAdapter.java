package faang.school.postservice.service.cache;

import faang.school.postservice.config.properties.PostCacheProperties;
import faang.school.postservice.service.cache.model.PostCacheDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisPostCacheAdapter implements PostCachePort {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheProperties props;
    @Override
    public void put(PostCacheDto post) {
        if (post == null || post.id() == null) return;
        String key = key(post.id());
        Duration ttl = props.getTtl();
        if (ttl != null && !ttl.isZero() && !ttl.isNegative()) {
            redisTemplate.opsForValue().set(key, post, ttl);
        } else {
            redisTemplate.opsForValue().set(key, post);
        }
    }

    @Override
    public PostCacheDto get(Long postId) {
        if (postId == null) return null;
        Object value = redisTemplate.opsForValue().get(key(postId));
        return (value instanceof PostCacheDto dto) ? dto : null;
    }

    @Override
    public void evict(Long postId) {
        if (postId == null) return;
        redisTemplate.delete(key(postId));
    }

    private String key(Long postId) {
        return props.getKeyPrefix() + ":" + postId;
    }
}
