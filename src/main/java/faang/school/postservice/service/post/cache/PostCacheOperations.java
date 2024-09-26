package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.service.post.hash.tag.PostHashTagService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostCacheOperations {
    private static final String PONG = "PONG";

    @Value("${app.post.cache.post_id_prefix}")
    private String postIdPrefix;

    private final PostHashTagService postHashTagService;
    private final PostCacheOperationsTries postCacheOperationsTries;
    private final RedisTemplate<String, PostCacheDto> redisTemplatePost;
    private final ZSetOperations<String, String> zSetOperations;

    public Set<String> findIdsByHashTag(String tag, int start, int end) {
        try {
            return zSetOperations.reverseRange(tag, start, end);
        } catch (RedisConnectionFailureException exception) {
            log.error("Find ids by hash tag {} in cache failure {}: {}", tag, exception.getClass(),
                    exception.getMessage());
            return new HashSet<>();
        }
    }

    public List<PostCacheDto> findAllByIds(List<String> ids) {
        try {
            return redisTemplatePost.opsForValue().multiGet(ids);
        } catch (RedisConnectionFailureException exception) {
            log.error("Find all post in cache by ids {} failure {}: {}", ids.toString(), exception.getClass(),
                    exception.getMessage());
            return new ArrayList<>();
        }
    }

    public void addPostToCacheByTag(PostCacheDto post, List<String> newTags, String tagToFind) {
        log.info("Add post to cache by tag: {}, post with id: {}", tagToFind, post.getId());
        String postId = postIdPrefix + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        boolean toDeletePostFromCache = false;
        newTags = filterByTagsInCache(newTags, tagToFind);

        if (!newTags.isEmpty()) {
            saveChangesOfPost(post, postId, timestamp, newTags, List.of(), toDeletePostFromCache);
        }
    }

    public void addPostToCache(PostCacheDto post, List<String> newTags) {
        log.info("Add post to cache, post with id: {}", post.getId());
        String postId = postIdPrefix + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        boolean toDeletePostFromCache = false;
        newTags = filterByTagsInCache(newTags);

        if (!newTags.isEmpty()) {
            saveChangesOfPost(post, postId, timestamp, newTags, List.of(), toDeletePostFromCache);
        }
    }

    public void deletePostOfCache(PostCacheDto post, List<String> primalTags) {
        log.info("Delete post of cache, post with id: {}", post.getId());
        String postId = postIdPrefix + post.getId();
        long timestamp = 0;
        boolean toDeletePostFromCache = true;
        primalTags = filterByTagsInCache(primalTags);

        if (!primalTags.isEmpty() || postIsInCache(postId)) {
            saveChangesOfPost(post, postId, timestamp, List.of(), primalTags, toDeletePostFromCache);
        }
    }

    public void updatePostOfCache(PostCacheDto post, List<String> primalTags, List<String> updTags) {
        log.info("Update post of cache, post with id: {}", post.getId());
        String postId = postIdPrefix + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        List<String> updTagsOfPostInCache = filterByTagsInCache(updTags);
        List<String> delTags = postHashTagService.getDeletedHashTags(primalTags, updTags);
        List<String> newTags = postHashTagService.getNewHashTags(primalTags, updTags);
        delTags = filterByTagsInCache(delTags);
        newTags = filterByTagsInCache(newTags);

        boolean toDeletePostFromCache = newTags.isEmpty() && updTagsOfPostInCache.isEmpty();

        if (!delTags.isEmpty() || !newTags.isEmpty() || postIsInCache(postId)) {
            saveChangesOfPost(post, postId, timestamp, newTags, delTags, toDeletePostFromCache);
        }
    }

    public boolean isRedisConnected() {
        try {
            String pingResponse = Objects.requireNonNull(redisTemplatePost.getConnectionFactory()).getConnection().ping();
            return PONG.equals(pingResponse);
        } catch (RedisConnectionFailureException exception) {
            log.error("{} : {}", exception.getClass(), exception.getMessage());
            return false;
        }
    }

    private void saveChangesOfPost(PostCacheDto post, String postId, long timestamp, List<String> newTags,
                                   List<String> delTags, boolean toDeletePost) {
        redisTemplatePost.execute(new SessionCallback<>() {
            @Override
            public List<Object> execute(@NonNull RedisOperations operations) throws DataAccessException {
                try {
                    redisTemplatePost.watch(postId);
                    delTags.forEach(redisTemplatePost::watch);
                    newTags.forEach(redisTemplatePost::watch);
                    redisTemplatePost.setEnableTransactionSupport(true);
                    return postCacheOperationsTries
                            .tryToSaveChangesOfPost(post, postId, timestamp, newTags, delTags, toDeletePost);
                } finally {
                    redisTemplatePost.setEnableTransactionSupport(false);
                    redisTemplatePost.unwatch();
                }
            }
        });
    }

    public List<String> filterByTagsInCache(List<String> tags) {
        try {
            return postCacheOperationsTries.tryFilterByTagsInCache(tags, null);
        } catch (RedisConnectionFailureException exception) {
            log.error("Compare with tags in cache failure {}: {}", exception.getClass(), exception.getMessage());
            return new ArrayList<>();
        }
    }

    private List<String> filterByTagsInCache(List<String> tags, String tagToFind) {
        try {
            return postCacheOperationsTries.tryFilterByTagsInCache(tags, tagToFind);
        } catch (RedisConnectionFailureException exception) {
            log.error("Compare with tags in cache failure by tag {}: {}", exception.getClass(), exception.getMessage());
            return new ArrayList<>();
        }
    }

    private boolean postIsInCache(String postId) {
        try {
            return Boolean.TRUE.equals(redisTemplatePost.hasKey(postId));
        } catch (RedisConnectionFailureException exception) {
            log.error("Check for post is in cache {}: {}", exception.getClass(), exception.getMessage());
            return false;
        }
    }
}
