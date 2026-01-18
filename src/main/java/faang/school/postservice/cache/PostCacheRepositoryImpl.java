package faang.school.postservice.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.FeedRedisProperties;
import faang.school.postservice.dto.cache.PostCacheDto;
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
public class PostCacheRepositoryImpl implements PostCacheRepository {

    private static final Duration TTL = Duration.ofHours(2);

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final FeedRedisProperties props;

    @Override
    public Map<Long, PostCacheDto> findAllByIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Map.of();
        }

        List<String> keys = postIds.stream()
                .map(this::key)
                .toList();

        List<String> values = redis.opsForValue().multiGet(keys);

        Map<Long, PostCacheDto> result = new HashMap<>();
        if (values == null) {
            return result;
        }

        for (int i = 0; i < values.size(); i++) {
            String json = values.get(i);
            if (json == null) {
                continue;
            }

            try {
                PostCacheDto dto = objectMapper.readValue(json, PostCacheDto.class);
                result.put(postIds.get(i), dto);
            } catch (Exception e) {
                log.debug("Failed to deserialize PostCacheDto for key={}", keys.get(i), e);
            }
        }

        return result;
    }

    @Override
    public void saveAll(Map<Long, PostCacheDto> postsById) {
        if (postsById == null || postsById.isEmpty()) {
            return;
        }

        postsById.forEach((id, dto) -> {
            try {
                redis.opsForValue().set(
                        key(id),
                        objectMapper.writeValueAsString(dto),
                        TTL
                );
            } catch (JsonProcessingException e) {
                log.debug("Failed to serialize PostCacheDto id={}", id, e);
            }
        });
    }

    private String key(Long postId) {
        return props.getKeyPrefix() + "posts:" + postId;
    }
}
