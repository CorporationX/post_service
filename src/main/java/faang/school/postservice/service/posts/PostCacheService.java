package faang.school.postservice.service.posts;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.posts.mapper.PostRedisMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;
    private final PostRedisMapper postRedisMapper;

    public void save(Post post) {
        String key = redisProperties.getPostsKeyPrefix() + ":" + post.getId();
        redisTemplate.opsForValue().set(
                key,
                postRedisMapper.toRedis(post),
                Duration.ofSeconds(redisProperties.getPostsTtlSeconds())
        );
    }
}