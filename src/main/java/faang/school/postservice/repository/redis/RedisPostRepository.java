package faang.school.postservice.repository.redis;

import faang.school.postservice.config.properties.CachedPostHashKeysProperties;
import faang.school.postservice.util.JsonMapperUtil;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CachedComment;
import faang.school.postservice.model.redis.CachedLike;
import faang.school.postservice.model.redis.CachedPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisPostRepository {
    private static final String POST_PREFIX = "post_";
    private static final String LIKES_PREFIX = ":likes";
    private static final String COMMENT_PREFIX = ":comments";
    private static final long COMMENT_THRESHOLD = 3L;
    private final RedisTemplate<String, Object> hashTemplate;
    private final RedisTemplate<String, String> stringTemplate;
    private final CachedPostHashKeysProperties properties;
    private final JsonMapperUtil jsonMapper;

    @Value("${spring.data.redis.ttl-hours}")
    private long ttlHours;

    public void savePost(Post post) {
        String key = POST_PREFIX + post.getId();
        hashTemplate.opsForHash().put(key, properties.postId(), post.getId());
        hashTemplate.opsForHash().put(key, properties.content(), post.getContent());
        hashTemplate.opsForHash().put(key, properties.authorId(), post.getAuthorId());
        hashTemplate.opsForHash().put(key, properties.projectId(), post.getProjectId());
        hashTemplate.opsForHash().put(key, properties.publishedAt(), post.getPublishedAt());
        hashTemplate.opsForHash().put(key, properties.updatedAt(), post.getUpdatedAt());
        hashTemplate.opsForHash().put(key, properties.views(), 0);
        hashTemplate.opsForHash().put(key, properties.likesCount(), 0);
        hashTemplate.expire(key, ttlHours, TimeUnit.HOURS);
        log.info("Saved post in Redis");
    }

    public void addLike(Long postId, CachedLike like) {
        String key = POST_PREFIX + postId + LIKES_PREFIX;
        String json = jsonMapper.mapToJson(like);
        stringTemplate.opsForSet().add(key, json);
        incrementLikesCount(postId);
        log.info("Like added to post {}", postId);
    }

    public List<CachedLike> getLikes(Long postId) {
        String key = POST_PREFIX + postId + LIKES_PREFIX;
        Set<String> likesJson = stringTemplate.opsForSet().members(key);
        List<CachedLike> likes = new ArrayList<>();
        for (String like : likesJson) {
            CachedLike cachedLike = jsonMapper.mapToObject(like, CachedLike.class);
            likes.add(cachedLike);
        }
        return likes;
    }

    public void incrementViews(Long postId) {
        String key = POST_PREFIX + postId;
        String hashKey = "views";
        hashTemplate.opsForHash().increment(key, hashKey, 1);
    }

    public CachedPost getPost(Long postId) {
        String key = POST_PREFIX + postId;
        Map<Object, Object> hash = hashTemplate.opsForHash().entries(key);
        CachedPost post = jsonMapper.convertToObject(hash, CachedPost.class);
        if (post == null) {
            log.warn("Post with id: {} was not found", postId);
            return null;
        }
        List<CachedComment> comments = getComments(postId);
        List<CachedLike> likes = getLikes(postId);
        post.setComments(comments);
        post.setLikes(likes);
        hashTemplate.expire(key, ttlHours, TimeUnit.HOURS);

        return post;
    }

    public void addComment(Long postId, CachedComment comment) {
        String key = POST_PREFIX + postId + COMMENT_PREFIX + comment.getId();
        long timestamp = System.currentTimeMillis();
        String commentJson = jsonMapper.mapToJson(comment);
        stringTemplate.opsForZSet().add(key, commentJson, timestamp);
        log.info("Comment added to post {}: {}", postId, commentJson);
        long commentsCount = stringTemplate.opsForZSet().size(key);
        if (commentsCount > COMMENT_THRESHOLD) {
            stringTemplate.opsForZSet().removeRange(key, 0, -4);
        }
    }


    public List<CachedComment> getComments(Long postId) {
        String key = POST_PREFIX + postId + COMMENT_PREFIX;
        Set<String> commentsJson = stringTemplate.opsForZSet().range(key, 0, 3);
        List<CachedComment> comments = new ArrayList<>();
        for (String commentJson : commentsJson) {
            CachedComment cachedComment = jsonMapper.mapToObject(commentJson, CachedComment.class);
            comments.add(cachedComment);
        }
        return comments;
    }

    private void incrementLikesCount(Long postId) {
        String key = POST_PREFIX + postId;
        hashTemplate.opsForHash().increment(key, properties.likesCount(), 1);
    }
}