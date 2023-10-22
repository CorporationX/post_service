package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisUserRepository {
    private final RedisTemplate<Long, UserDto> redisTemplate;

    public void save(long userId, UserDto userDto) {
        redisTemplate.opsForValue().set(userId, userDto);
        log.info("User was successfully saved {}", userDto);
    }

    @Cacheable(value = "userCache", key = "#userId")
    public UserDto getUserById(long userId) {
        UserDto user = redisTemplate.opsForValue().get(userId);
        if (user != null) {
            log.info("User with ID {} found in cache", userId);
            redisTemplate.expire(userId, 1, TimeUnit.DAYS);
        } else {
            log.info("User with ID {} not found in cache", userId);
        }
        return user;
    }
}
