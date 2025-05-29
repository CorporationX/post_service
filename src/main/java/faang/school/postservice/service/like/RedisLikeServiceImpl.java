package faang.school.postservice.service.like;

import faang.school.postservice.exception.LikeOptimisticLockException;
import faang.school.postservice.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static faang.school.postservice.contants.ErrorMessage.ERROR_MAX_ATTEMPTS_EXCEEDED;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NOT_FOUND_POST;


@Service
@Slf4j
@RequiredArgsConstructor
public class RedisLikeServiceImpl implements RedisLikeService {
    private static final String POST_KEY_PREFIX = "post:";
    private static final String LIKES_FIELD = "likes";
    private static final String VERSION_FIELD = "version";
    private static final int MAX_ATTEMPTS = 3;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void incrementLikesForPost(long postId) {
        final String key = POST_KEY_PREFIX + postId;
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            attempts++;
            stringRedisTemplate.watch(key);
            if (!stringRedisTemplate.hasKey(key)) {
                stringRedisTemplate.unwatch();
                throw new PostNotFoundException(ERROR_NOT_FOUND_POST);
            }

            String currentVersion = Objects.toString(stringRedisTemplate.opsForHash().get(key, VERSION_FIELD), "0");
            String newVersion = Long.toString(Long.parseLong(currentVersion) + 1);

            stringRedisTemplate.multi();
            stringRedisTemplate.opsForHash().increment(key, LIKES_FIELD, 1);
            stringRedisTemplate.opsForHash().put(key, VERSION_FIELD, newVersion);

            List<Object> results = stringRedisTemplate.exec();
            if (results != null) {
                log.debug("Likes incremented for post {}", postId);
                return;
            }
            log.debug("Optimistic lock conflict for post {}, attempt {}/{}", postId, attempts, MAX_ATTEMPTS);
        }
        throw new LikeOptimisticLockException(String.format(ERROR_MAX_ATTEMPTS_EXCEEDED, postId, MAX_ATTEMPTS));
    }
}

