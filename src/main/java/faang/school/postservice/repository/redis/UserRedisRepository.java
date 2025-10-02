package faang.school.postservice.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.UserRedisDto;
import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
public class UserRedisRepository {

    private static final String USER_TEMPLATE = "user:%s";

    @Value("${redis.schema.user.ttl-day}")
    private int ttlDay;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserMapper mapper;
    private final ObjectMapper objectMapper;

    public UserRedisRepository(@Qualifier("redisDataTemplate") RedisTemplate<String, Object> redisTemplate,
                               UserMapper mapper,
                               ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    public void saveUser(UserViewDto user) {
        String key = getFormattedKey(user.id());
        UserRedisDto userRedisDto = mapper.toRedisDto(user);
        redisTemplate.opsForValue().set(key, userRedisDto, Duration.ofDays(ttlDay));
        log.info("Пользователь был сохранен в Redis");
    }

    public void saveUsers(List<UserViewDto> users) {
        try {
            Map<String, UserRedisDto> userMap = new HashMap<>();
            for (UserViewDto user : users) {
                UserRedisDto userRedisDto = mapper.toRedisDto(user);
                String key = getFormattedKey(user.id());
                userMap.put(key, userRedisDto);
            }

            redisTemplate.opsForValue().multiSet(userMap);

            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (String key : userMap.keySet()) {
                    connection.expire(
                            Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(key)),
                            Duration.ofDays(ttlDay).getSeconds()
                    );
                }
                return null;
            });

            log.info("Сохранено {} пользователей в Redis с TTL {} дней",
                    users.size(), ttlDay);
        } catch (Exception e) {
            log.error("Ошибка при сохранении пользователей в Redis: {}", e.getMessage());
        }
    }

    public Optional<UserRedisDto> getUser(Long id) {
        String key = getFormattedKey(id);
        return Optional.ofNullable((UserRedisDto) redisTemplate.opsForValue().get(key));
    }

    public List<UserRedisDto> getUserByIds(List<Long> ids) {
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long userId : ids) {
                String key = getFormattedKey(userId);
                connection.get(Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(key)));
            }
            return null;
        });

        return results.stream()
                .filter(Objects::nonNull)
                .map(obj -> objectMapper.convertValue(obj, UserRedisDto.class))
                .toList();
    }

    private String getFormattedKey(Long id) {
        return String.format(USER_TEMPLATE, id);
    }
}