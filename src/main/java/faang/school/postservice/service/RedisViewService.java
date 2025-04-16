package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisViewService {

    private static final String POST_HASH_PREFIX = "post:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void incrementViewCount(Long postId) {
        String key = POST_HASH_PREFIX + postId;
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            hashOps.increment(key, "views", 1L);
            log.info("View count incremented for post {}", postId);
        } else {
            log.warn("Post {} not found in Redis", postId);
        }
    }
}