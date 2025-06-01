package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @CachePut(key = "#userDto.id")
    public UserDto saveUser(UserDto userDto) {
        return userDto;
    }

    public Map<Long, UserDto> findByIds(List<Long> authorIds) {
        List<String> keys = authorIds.stream()
                .map(id -> "users::" + id)
                .toList();

        List<Object> cachedObjects = redisTemplate.opsForValue().multiGet(keys);

        Map<Long, UserDto> result = new LinkedHashMap<>();
        if (cachedObjects != null) {
            int size = Math.min(authorIds.size(), cachedObjects.size());
            for (int i = 0; i < size; i++) {
                Object cachedObject = cachedObjects.get(i);
                if (cachedObject instanceof UserDto user) {
                    result.put(authorIds.get(i), user);
                }
            }
        }
        return result;
    }
}
