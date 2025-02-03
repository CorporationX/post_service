package faang.school.postservice.repository.rediscache;

import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long TTL_SECONDS = 86400;

    public void save(String key, Object value) {
        log.info("Saving user with ID: {} to Redis", key);
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(TTL_SECONDS));
    }

    public UserDto findUserByKey(String key) {
        log.info("Fetching user with ID: {} from Redis", key);
        return (UserDto) redisTemplate.opsForValue().get(key);
    }
}
