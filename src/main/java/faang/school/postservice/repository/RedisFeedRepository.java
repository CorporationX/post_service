package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisFeedRepository {
    @Value("${linked-hash-set-size}")
    private int size;
    private final RedisTemplate<Long, LinkedHashSet<Long>> redisTemplate;

    public void save(long postId, long postDto) {
        LinkedHashSet<Long> postIds = redisTemplate.opsForValue().get(postId);
        if (postIds == null) {
            postIds = new LinkedHashSet<>(size);
        }

        if (postIds.size() == 500) {
            postIds.remove(postIds.iterator().next());
        }

        postIds.add(postDto);
        boolean updated = redisTemplate.opsForValue().setIfPresent(postId, postIds);

        if (updated) {
            redisTemplate.expire(postId, 1, TimeUnit.DAYS);
            log.info("Post was successfully saved {}", postDto);
        } else {
            log.warn("Optimistic lock failed for post {}", postId);
        }
    }

    @Cacheable(value = "postCache", key = "#postId")
    public LinkedHashSet<Long> getPostById(long postId) {
        LinkedHashSet<Long> postIds = redisTemplate.opsForValue().get(postId);
        if (postIds != null) {
            log.warn("Post with ID {} found in cache", postId);
        } else {
            log.warn("Post with ID {} not found in cache", postId);
        }
        return postIds;
    }
}
