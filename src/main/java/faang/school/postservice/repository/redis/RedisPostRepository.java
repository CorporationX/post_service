package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.CachedPost;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisPostRepository {
    private static final String POST_PREFIX = "post_";
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.ttl-hours}")
    private long ttlHours;
    private HashOperations<String, String, CachedPost> hashOpsPosts;

    @PostConstruct
    public void init() {
        hashOpsPosts = redisTemplate.opsForHash();
    }

    public void savePost(CachedPost post) {
        hashOpsPosts.put(POST_PREFIX + post.getId(), post.getId().toString(), post);
        redisTemplate.expire(POST_PREFIX + post.getId(), ttlHours, TimeUnit.HOURS);
        log.info("Saved post in Redis");
    }

    public void updatePost(CachedPost post) {
        hashOpsPosts.put(POST_PREFIX + post.getId(), post.getId().toString(), post);
        log.info("Updated post in Redis");
    }

    public CachedPost getPost(Long postId) {
        return hashOpsPosts.get(POST_PREFIX + postId, postId);
    }
}