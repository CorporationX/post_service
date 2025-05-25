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
    private long timeToLive;

    @Value("${spring.data.redis.feed.size}")
    private long feedSize;

    private static final String POSTS_HASH_KEY = "posts:";
    private static final String USER_FEED_KEY = "user:feed:%s";

    public void cachePost(CachedPost post) {
        redisTemplate.opsForHash().put(
                POSTS_HASH_KEY,
                post.getId().toString(),
                post
        );

        redisTemplate.expire(
                POSTS_HASH_KEY + post.getId().toString(),
                timeToLive,
                TimeUnit.SECONDS
        );
    }

    public void addToUserFeed(CachedPost post, Long followerId) {
        String key = String.format(USER_FEED_KEY, followerId);
        stringRedisTemplate.opsForZSet().add(
                key,
                post.getId().toString(),
                post.getPublishedAt().toEpochMilli()
        );
        stringRedisTemplate.opsForZSet().removeRange(key, 0, -feedSize - 1);
    }

}
