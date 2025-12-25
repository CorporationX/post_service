package faang.school.postservice.repository.cache;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.cache.UserCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserCacheRepository {
    @Value("${redis.cache.user.key-prefix}")
    private String keyPrefix;

    @Value("${redis.cache.user.ttl-days}")
    private int ttlDays;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserServiceClient userServiceClient;

    public void save(UserCacheDto user) {
        String key = keyPrefix + user.id();

        try {
            redisTemplate.opsForHash().put(key, "id", user.id());
            redisTemplate.opsForHash().put(key, "name", user.name());
            redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
            log.info("User {} added to cache", user);
        } catch (Exception exception) {
            log.error("Failed to add user {} to cache:", user, exception);
        }
    }

    public Optional<UserCacheDto> findById(Long userId) {
        String key = keyPrefix + userId;

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                UserCacheDto.builder()
                        .id(Long.valueOf(entries.get("id").toString()))
                        .name((String) entries.get("name"))
                        .build()
        );
    }

}


