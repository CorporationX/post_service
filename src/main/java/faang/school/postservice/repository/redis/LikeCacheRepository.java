package faang.school.postservice.repository.redis;

import faang.school.postservice.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeCacheRepository {

    private final RedisTemplate<String, Object> objectRedisTemplate;

    @Value("${cache.post.key-prefix}")
    private String postKeyPrefix;

    @Value("${cache.post.likes-count-postfix}")
    private String likesCountPostfix;

    @Value("${cache.post.liked-users-postfix}")
    private String likedUsersPostfix;

    public void incrementLikesPost(long postId, long likedUserId) {
        String postKey = createKey(postId, null);
        String likedUsersKey = createKey(postId, likedUsersPostfix);
        String likesCountKey = createKey(postId, likesCountPostfix);

        objectRedisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(@NotNull RedisOperations operations) throws DataAccessException {
                if (!operations.hasKey(postKey)) {
                    throw new PostNotFoundException("Post not found in cache. ID: " + postId);
                }

                operations.watch(likedUsersKey);

                if (Boolean.TRUE.equals(operations.opsForSet().isMember(likedUsersKey, likedUserId))) {
                    log.warn("User {} already like post {}", likedUserId, postId);
                    return null;
                }

                operations.multi();
                operations.opsForSet().add(likedUsersKey, likedUserId);
                operations.opsForValue().increment(likesCountKey);

                if (operations.exec() == null) {
                    throw new OptimisticLockingFailureException("Concurrent modification error likes count for post " + postId);
                }

                return null;
            }
        });
    }

    private String createKey(long postId, String keyPostfix) {
        if (keyPostfix == null) {
            return postKeyPrefix + postId;
        }

        return postKeyPrefix + postId + keyPostfix;
    }
}
