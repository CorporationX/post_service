package faang.school.postservice.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.FeedRedisProperties;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepositoryImpl implements UserCacheRepository {


    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final FeedRedisProperties props;

    @Override
    public Map<Long, UserDto> findAllByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        List<String> keys = userIds.stream()
                .map(this::key)
                .toList();

        List<String> values = redis.opsForValue().multiGet(keys);

        Map<Long, UserDto> result = new HashMap<>();
        if (values == null) {
            return result;
        }

        for (int i = 0; i < values.size(); i++) {
            String json = values.get(i);
            if (json == null) {
                continue;
            }

            try {
                UserDto dto = objectMapper.readValue(json, UserDto.class);
                result.put(userIds.get(i), dto);
            } catch (Exception e) {
                log.debug("Failed to deserialize UserDto for key={}", keys.get(i), e);
            }
        }

        return result;
    }

    @Override
    public void saveAll(Map<Long, UserDto> usersById) {
        if (usersById == null || usersById.isEmpty()) {
            return;
        }

        usersById.forEach((id, dto) -> {
            try {
                redis.opsForValue().set(
                        key(id),
                        objectMapper.writeValueAsString(dto),
                        props.getUserTtl()
                );
            } catch (JsonProcessingException e) {
                log.debug("Failed to serialize UserDto id={}", id, e);
            }
        });
    }

    private String key(Long userId) {
        String prefix = props.getKeyPrefix();
        if (!prefix.endsWith(":")) prefix += ":";
        return prefix + "users:" + userId;
    }
}
