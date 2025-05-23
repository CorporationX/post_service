package faang.school.postservice.service;

import faang.school.postservice.entity.CachedPost;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${spring.data.redis.ttl.post}")
    private int timeToLive;

    private static final String POSTS_HASH_KEY = "posts";
    private static final String POSTS_ZSET_KEY = "user:feed";

    public void cachePost(CachedPost post) {
        redisTemplate.opsForHash().put(
                POSTS_HASH_KEY,
                post.getId().toString(),
                post
        );

        redisTemplate.expire(
                POSTS_HASH_KEY + ":" + post.getId(),
                timeToLive,
                TimeUnit.SECONDS);

        stringRedisTemplate.opsForZSet().add(
                POSTS_ZSET_KEY + post.getAuthorId(),
                post.getId().toString(),
                post.getPublishedAt().toEpochMilli()
        );

        stringRedisTemplate.expire(
                POSTS_ZSET_KEY + post.getAuthorId(),
                timeToLive,
                TimeUnit.SECONDS
        );
    }
}
