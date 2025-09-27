package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.redis.UserRedisDto;
import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * Репозиторий для сохранения и получения пользователей в/из Redis.
 * Используется для формирования Feed.
 *
 * @author Linempy
 * @since 27.09.2025
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRedisRepository {

    @Value("${redis.schema.user.key}")
    private String keyUser;

    @Value("${redis.schema.user.ttl-day}")
    private int ttlDay;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserMapper mapper;

    public void saveUser(UserViewDto user) {
        String key = getKey(user.id());
        UserRedisDto userRedisDto = mapper.toRedisDto(user);
        redisTemplate.opsForValue().set(key, userRedisDto, Duration.ofDays(ttlDay));
        log.info("Пользователь был сохранен в Redis");
    }

    public Optional<UserRedisDto> getUser(Long id) {
        String key = getKey(id);
        return Optional.ofNullable((UserRedisDto) redisTemplate.opsForValue().get(key));
    }

    private String getKey(Long id) {
        return String.format(keyUser, id);
    }
}