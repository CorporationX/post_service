package faang.school.postservice.repository;

import faang.school.postservice.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static faang.school.postservice.service.PostService.NO_POST_FOUND;

@Component
@Slf4j
@RequiredArgsConstructor
public class FeedRedisRepository {
    private static final String KEY_PREFIX = "feed:";
    private final ZSetOperations<String, String> zSetOps;

    @Value("${spring.data.redis.feed.post-max-size}")
    private int postMaxSize;

    public void save(Long userId , Long postId) {
        String key = KEY_PREFIX + userId;
        double score = -Instant.now().toEpochMilli();
        zSetOps.add(key, postId.toString(), score);
        zSetOps.removeRange(key, postMaxSize, -1);
    }

    public Set<Long> getInRangeByUserIdAndPostId(Long userId, Long postId, int postBatchSize) {
        String key = KEY_PREFIX + userId;
        Long rank = zSetOps.rank(key, postId.toString());

        if (rank == null) {
            log.info(NO_POST_FOUND);
            throw new PostNotFoundException(NO_POST_FOUND);
        }

        Set<Long> postIds = new HashSet<>();
        for (String str : Objects.requireNonNull(zSetOps.range(key, rank + 1, rank + postBatchSize))) {
            try {
                Long id = Long.valueOf(str);
                postIds.add(id);
            } catch (NumberFormatException | NullPointerException e) {
                break;
            }
        }

        return postIds;
    }

    public Set<Long> getFirstInRangeByUserId(Long userId, int postBatchSize) {
        Set<Long> postIds = new HashSet<>();
        for (String str : Objects.requireNonNull(zSetOps.range(KEY_PREFIX + userId, 0, postBatchSize))) {
            try {
                Long id = Long.valueOf(str);
                postIds.add(id);
            } catch (NumberFormatException | NullPointerException e) {
                break;
            }
        }

        return postIds;
    }
}
