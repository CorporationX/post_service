package faang.school.postservice.utils;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostCache {
    private final RedisTemplate<Long, Post> redisTemplate;

    @Value("${cache.expiration-hours}")
    private int expirationHours;

    public void save(Post post) {
        redisTemplate.opsForValue().setIfAbsent(post.getId(), post, Duration.ofHours(expirationHours));
    }

    public Post get(Long id) {
        return redisTemplate.opsForValue().get(id);
    }

    public boolean delete(Long id) {
        return redisTemplate.delete(id);
    }

    public Long deleteAll(List<Long> ids) {
        return redisTemplate.delete(ids);
    }
}