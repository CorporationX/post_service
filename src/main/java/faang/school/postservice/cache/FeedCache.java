package faang.school.postservice.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;
    private final ZSetOperations<String, String> zSetOperations;

    @Value("${spring.data.redis.keys.feed}")
    private String feedKeyName;
    @Value("${spring.data.redis.properties.ttl}")
    private int ttl;

    @Value("${spring.data.redis.properties.feed-size}")
    private int feedSize;

    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100, multiplier = 3))
    public void save(Long userId, Long postId) {
        String feedKey = prepareFeedKey(userId);
        AtomicReference<String> zSetKeyFeedValue = new AtomicReference<>(getByKey(feedKey));

        Boolean success = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            connection.watch(feedKey.getBytes());
            connection.multi();

            if (zSetKeyFeedValue.get() == null) {
                zSetKeyFeedValue.set(prepareZSetKeyFeedValue(feedKey));
                connection.hashCommands().hSet(feedKeyName.getBytes(), feedKey.getBytes(), zSetKeyFeedValue.get().getBytes());
            }

            long score = Instant.now().getEpochSecond();

            connection.zAdd(zSetKeyFeedValue.get().getBytes(), score, postId.toString().getBytes());
            connection.keyCommands().expire(zSetKeyFeedValue.get().getBytes(), ttl);
            connection.exec();
            return true;
        });

        if (success == null || !success) {
            throw new OptimisticLockingFailureException(String.format("Unsuccessfully trying to save feed for user %s", userId));
        }

        log.info(String.format("Successfully trying to save post to feed cache for %s user", userId));
    }

    public String getByKey(String feedKey) {
        return hashOperations.get(feedKeyName, feedKey);
    }

    public Optional<Set<String>> getByRange(Long userId, Long postId) {
        Set<String> postsId;
        String feedKey = prepareFeedKey(userId);
        String zSetKeyFeedValue = getByKey(feedKey);

        if (zSetKeyFeedValue == null) {
            log.warn(String.format("Feed %s doesn't have set of posts", feedKey));
            return Optional.empty();
        }

        Double score = zSetOperations.score(zSetKeyFeedValue, postId.toString());

        if (score == null) {
            log.warn(String.format("Set of posts %s doesn't have post %s", zSetKeyFeedValue, postId));
            return Optional.empty();
        }

        double maxScore = score - 1;
        double minScore = Double.MIN_VALUE;

        postsId = zSetOperations.reverseRangeByScore(zSetKeyFeedValue, minScore, maxScore, 0, feedSize);


        return Optional.ofNullable(postsId);
    }

    private String prepareFeedKey(Long userId) {
        return String.format("%s_%s", feedKeyName, userId);
    }

    private String prepareZSetKeyFeedValue(String feedKey) {
        return String.format("%s_posts", feedKey);
    }
}
