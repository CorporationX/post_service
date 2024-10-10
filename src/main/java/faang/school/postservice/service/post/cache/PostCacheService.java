package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostCacheService {
    private static final String PONG = "PONG";

    @Value("${app.post.cache.number_of_top_in_cache}")
    private int numberOfTopInCache;

    @Value("${app.post.cache.post_id_prefix}")
    private String postIdPrefix;

    private final RedisTemplate<String, PostCacheDto> redisTemplatePost;
    private final ZSetOperations<String, String> zSetOperations;

    @Transactional
    public void savePostsByTag(String tag, List<PostCacheDto> posts) {
        log.info("Save posts to cache by tag: {}", tag);
        posts.forEach(post -> {
            String postId = postIdPrefix + post.getId();
            long timeStamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            redisTemplatePost.opsForValue().set(postId, post);
            zSetOperations.add(tag, postId, timeStamp);
            zSetOperations.removeRange(tag, 0, (numberOfTopInCache + 1) * -1);
        });
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveChangesOfPost(PostCacheDto post, String postId, long timestamp, List<String> newTags,
                                  List<String> delTags, boolean toDeletePost) {
        log.info("Save changes of post: {}", post);
        if (toDeletePost) {
            redisTemplatePost.delete(postId);
        } else {
            redisTemplatePost.opsForValue().set(postId, post);
        }
        delTags.forEach(tag -> zSetOperations.remove(tag, postId));
        newTags.forEach(tag -> {
            zSetOperations.add(tag, postId, timestamp);
            zSetOperations.removeRange(tag, 0, (numberOfTopInCache + 1) * -1);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<PostCacheDto> findInRangeByHashTag(String tag, int start, int end) {
        log.info("Find posts by tag: {}, in range, start: {}, end: {}", tag, start, end);
        try {
            Set<String> postIds = zSetOperations.reverseRange(tag, start, end);
            if (postIds != null) {
                return redisTemplatePost.opsForValue().multiGet(postIds);
            } else {
                log.warn("Ids by tag: {} is null", tag);
                return List.of();
            }
        } catch (RedisConnectionFailureException exception) {
            log.error("Find posts in range by hash tag failure, tag: {}, message:", tag, exception);
            return new ArrayList<>();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<String> filterByTagsInCache(List<String> tags) {
        return tags
                .stream()
                .filter(tag -> Boolean.TRUE.equals(redisTemplatePost.hasKey(tag)))
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean postIsInCache(String postId) {
        return Boolean.TRUE.equals(redisTemplatePost.hasKey(postId));
    }

    public boolean isRedisConnected() {
        try {
            log.info("Redis connection check");
            String pingResponse =
                    Objects.requireNonNull(redisTemplatePost.getConnectionFactory()).getConnection().ping();
            return PONG.equals(pingResponse);
        } catch (RedisConnectionFailureException exception) {
            log.error("Redis connection failure:", exception);
            return false;
        }
    }
}
