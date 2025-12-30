package faang.school.postservice.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCacheService {

    private static final String POST_KEY_PREFIX = "post:";
    private static final String VIEWS_FIELD = "views";

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> incrementViewsScript; 
    /**
     * Increments view count atomically using cached Lua script.
     * Structure in Redis:
     * post:123 -> Hash {
     *   "id": "123",
     *   "title": "...",
     *   "views": "42",
     *   "authorId": "1"
     * }
     * 
     * Uses atomic Lua script to handle concurrent updates safely.
     * The script checks if the post key exists and only then increments views.
     *
     * @param postId the ID of the post
     * @return true if the post exists in cache and view was incremented, false otherwise
     */
    public boolean incrementPostViews(Long postId) {
        String postKey = POST_KEY_PREFIX + postId;

        Long newViewsCount = redisTemplate.execute(
            incrementViewsScript,
            Collections.singletonList(postKey),
            VIEWS_FIELD
        );

        if (newViewsCount != null) {
            log.debug("Incremented views for post {}: {}", postId, newViewsCount);
            return true;
        }
        
        log.trace("Post {} not found in cache", postId);
        return false;
    }
}

