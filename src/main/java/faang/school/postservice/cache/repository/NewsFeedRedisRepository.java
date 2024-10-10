package faang.school.postservice.cache.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NewsFeedRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public void addPostId(String key, Long postId) {
        redisTemplate.opsForZSet().add(key, postId, -postId);
    }

    public List<Long> getSortedPostIds(String key) {
        Set<Object> postIds = redisTemplate.opsForZSet().range(key, 0, -1);
        if (postIds == null) {
            return new ArrayList<>();
        }
        return postIds.stream()
                .map(postId -> ((Number) postId).longValue())
                .collect(Collectors.toList());
    }

    public void removePostId(String key, Long postId) {
        redisTemplate.opsForZSet().remove(key, postId);
    }

    public void removeLastPostId(String key) {
        Objects.requireNonNull(redisTemplate.opsForZSet().range(key, -1, -1))
                .stream()
                .findFirst()
                .ifPresent(postId -> removePostId(key, (Long) postId));
    }

    public Long getSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }
}
