package faang.school.postservice.service;

import faang.school.postservice.entity.CachedPost;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${spring.data.redis.ttl.post}")
    private long ttl;

    @Value("${spring.data.redis.feed.size}")
    private long feedSize;

    @Value("${spring.data.redis.feed.global-size}")
    private long globalFeedSize;

    private static final String POSTS_HASH_KEY = "posts:";
    private static final String USER_FEED_KEY = "user:feed:%s";
    private static final String GLOBAL_FEED_KEY = "feed:top";

    public void cachePost(CachedPost post) {
        redisTemplate.opsForHash().put(
                POSTS_HASH_KEY,
                post.getId().toString(),
                post
        );

        redisTemplate.expire(
                POSTS_HASH_KEY + post.getId().toString(),
                ttl,
                TimeUnit.SECONDS
        );

        stringRedisTemplate.opsForZSet().add(GLOBAL_FEED_KEY, post.getId().toString(), post.getLikes());
        stringRedisTemplate.opsForZSet().removeRange(GLOBAL_FEED_KEY, 0, -globalFeedSize - 1);
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

    public List<CachedPost> getGlobalFeed(long size) {
        Set<String> topPostIds = stringRedisTemplate
                .opsForZSet()
                .reverseRange(GLOBAL_FEED_KEY, 0, size - 1);

        if (topPostIds == null || topPostIds.isEmpty()) {
            return List.of();
        }

        return topPostIds.stream()
                .map(id -> (CachedPost) redisTemplate
                        .opsForHash()
                        .get(POSTS_HASH_KEY, id))
                .filter(Objects::nonNull)
                .toList();

    }

}
