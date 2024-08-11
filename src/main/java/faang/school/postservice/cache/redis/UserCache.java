package faang.school.postservice.cache.redis;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

@Component
public class UserCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final SetOperations<String, Object> operations;

    @Value("${spring.data.redis.key-spaces.user.prefix}")
    private String keyPrefix;

    public UserCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.operations = redisTemplate.opsForSet();
    }

    public void save(UserDto userDto) {
        String key = keyPrefix + userDto.getId();
        operations.add(key, userDto);
    }
}