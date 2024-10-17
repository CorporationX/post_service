package faang.school.postservice.repository.feed;

import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisUserRepository {
    private static final String USER_KEY_PREFIX = "user:";
    private final RedisTemplate<String, Object> cacheRedisTemplate;

    @Value("${spring.data.redis.cache.ttl.user}")
    private long ttl;

    public void save(UserDto userDto) {
        String key = USER_KEY_PREFIX + userDto.getId();
        cacheRedisTemplate.opsForValue().set(key, userDto, Duration.ofSeconds(ttl));
    }

    public UserDto get(Long userId) {
        String key = USER_KEY_PREFIX + userId;
        return (UserDto) cacheRedisTemplate.opsForValue().get(key);
    }
}
