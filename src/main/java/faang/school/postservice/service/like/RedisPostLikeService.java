package faang.school.postservice.service.like;

import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.service.redis_lock.RedisDistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPostLikeService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisDistributedLock distributedLock;

    private static final String POST_LIKES_KEY = "post:likes:";
    private static final String POST_KEY = "post:";

    @Value("${app.redis.post-likes.ttl-days}")
    private long likesTtlDays;

    public void addLikeToPost(LikeEvent event) {
        Long postId = event.getPostId();
        String lockKey = "like:" + postId;
        String requestId = UUID.randomUUID().toString();

        if (!redisTemplate.hasKey(POST_KEY + postId)) {
            return;
        }

        try {
            if (!distributedLock.tryLockWithWait(lockKey, requestId)) {
                throw new IllegalStateException("Could not acquire lock for post " + postId);
            }

            String likesKey = POST_LIKES_KEY + postId;
            Long added = redisTemplate.opsForSet().add(likesKey, event.getLikedByUserId());

            if (added != null && added > 0) {
                redisTemplate.opsForHash().increment(POST_KEY + postId, "likeCount", 1);
                redisTemplate.expire(likesKey, likesTtlDays, TimeUnit.DAYS);
            }

        } finally {
            distributedLock.unlock(lockKey, requestId);
        }
    }
}
