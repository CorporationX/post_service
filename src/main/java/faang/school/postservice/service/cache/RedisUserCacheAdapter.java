package faang.school.postservice.service.cache;

import faang.school.postservice.config.properties.UserCacheProperties;
import faang.school.postservice.service.cache.model.UserCacheDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUserCacheAdapter implements UserCachePort {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserCacheProperties props;

    @Override
    public void put(UserCacheDto user) {
        if (user == null || user.id() == null) return;
        String key = key(user.id());
        Duration ttl = props.getTtl();
        if (ttl != null && !ttl.isZero() && !ttl.isNegative()) {
            redisTemplate.opsForValue().set(key, user, ttl);
        } else {
            redisTemplate.opsForValue().set(key, user);
        }
    }

    @Override
    public UserCacheDto get(Long userId) {
        if (userId == null) return null;
        Object value = redisTemplate.opsForValue().get(key(userId));
        return (value instanceof UserCacheDto u) ? u : null;
    }

    @Override
    public void evict(Long userId) {
        if (userId == null) return;
        redisTemplate.delete(key(userId));
    }

    private String key(Long userId) {
        return props.getKeyPrefix() + ":" + userId;
    }
}
