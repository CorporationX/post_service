package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.user.CacheUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisUserRepository {

    private static final String KEY_PREFIX = "users:";

    private final RedisTemplate<String, CacheUserDto> redisTemplate;

    @Value("${spring.data.redis.user-repository.ttl-days}")
    private Duration ttl;

    public void save(CacheUserDto cacheUserDto) {
        String key = KEY_PREFIX + cacheUserDto.getId();

        redisTemplate.opsForValue().set(
                key,
                cacheUserDto,
                ttl.toDays()
        );
    }

    public Optional<CacheUserDto> findById(Long userId) {
        String key = KEY_PREFIX + userId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }
}