package faang.school.postservice.repository.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisPostRepository {
    private static final String POST_KEY_PREFIX = "post:";
    private static final String COMMENT_KEY_SUFFIX = ":comments";
    private static final String COMMENT_HASH_SUFFIX = ":commentHash";
    private static final String LIKE_KEY_SUFFIX = ":likes";
    private static final String COMMENT_COUNT_SUFFIX = ":commentCount";
    private final RedisTemplate<String, Object> cacheRedisTemplate;

    @Value("${spring.data.redis.cache.ttl.post}")
    private long ttl;
    @Value("${spring.data.redis.cache.feed.showLastComments}")
    private int showLastComments;

    public void addNewPost(PostDto postDto) {
        Long postId = postDto.getId();
        String key = POST_KEY_PREFIX + postId;
        cacheRedisTemplate.opsForValue().set(key, postDto, Duration.ofSeconds(ttl));
    }

    public Optional<PostDto> getPost(Long postId) {
        String key = POST_KEY_PREFIX + postId;
        PostDto postDto = (PostDto) cacheRedisTemplate.opsForValue().get(key);
        return Optional.ofNullable(postDto);
    }

    public void deletePost(Long postId) {
        String key = POST_KEY_PREFIX + postId;
        cacheRedisTemplate.delete(key);
    }

    public void addComment(Long postId, CommentDto commentDto) {
        String zsetKey = POST_KEY_PREFIX + postId + COMMENT_KEY_SUFFIX;
        String hashKey = POST_KEY_PREFIX + postId + COMMENT_HASH_SUFFIX;

        double score = commentDto.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        cacheRedisTemplate.opsForZSet().add(zsetKey,commentDto.getId(), score);
        cacheRedisTemplate.opsForHash().put(hashKey, commentDto.getId().toString(), commentDto);
        cacheRedisTemplate.opsForZSet().removeRange(zsetKey, 0, -showLastComments - 1);
        cacheRedisTemplate.expire(zsetKey, Duration.ofSeconds(ttl));
        cacheRedisTemplate.expire(hashKey, Duration.ofSeconds(ttl));
    }

    public List<CommentDto> getComments(Long postId) {
        String zsetKey = POST_KEY_PREFIX + postId + COMMENT_KEY_SUFFIX;
        String hashKey = POST_KEY_PREFIX + postId + COMMENT_HASH_SUFFIX;

        Set<Object> commentIds = cacheRedisTemplate.opsForZSet().reverseRange(zsetKey, 0, -1);
        if (commentIds == null || commentIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> commentDtos = cacheRedisTemplate.opsForHash().multiGet(hashKey, commentIds);

        return commentDtos.stream()
                .filter(Objects::nonNull)
                .map(CommentDto.class::cast)
                .toList();
    }

    public void deleteComments(long postId) {
        String zsetKey = POST_KEY_PREFIX + postId + COMMENT_KEY_SUFFIX;
        String hashKey = POST_KEY_PREFIX + postId + COMMENT_HASH_SUFFIX;
        cacheRedisTemplate.delete(zsetKey);
        cacheRedisTemplate.delete(hashKey);
    }

    public void deleteComment(Long postId, Long commentId) {
        String zsetKey = POST_KEY_PREFIX + postId + COMMENT_KEY_SUFFIX;
        String hashKey = POST_KEY_PREFIX + postId + COMMENT_HASH_SUFFIX;

        cacheRedisTemplate.opsForZSet().remove(zsetKey, commentId);
        cacheRedisTemplate.opsForHash().delete(hashKey, commentId.toString());
    }


    public void incrementLike(Long postId) {
        String key = POST_KEY_PREFIX + postId + LIKE_KEY_SUFFIX;
        cacheRedisTemplate.opsForValue().increment(key);
    }

    public void decrementLike(Long postId) {
        String key = POST_KEY_PREFIX + postId + LIKE_KEY_SUFFIX;
        cacheRedisTemplate.opsForValue().decrement(key);
    }

    public void deleteLikeCounter(Long postId) {
        String key = POST_KEY_PREFIX + postId + LIKE_KEY_SUFFIX;
        cacheRedisTemplate.delete(key);
    }

    public void incrementComment(Long postId) {
        String key = POST_KEY_PREFIX + postId + COMMENT_COUNT_SUFFIX;
        cacheRedisTemplate.opsForValue().increment(key);
    }

    public void decrementComment(Long postId) {
        String key = POST_KEY_PREFIX + postId + COMMENT_COUNT_SUFFIX;
        cacheRedisTemplate.opsForValue().decrement(key);
    }

    public void deleteCommentCounter(Long postId) {
        String key = POST_KEY_PREFIX + postId + COMMENT_COUNT_SUFFIX;
        cacheRedisTemplate.delete(key);
    }

    public Long getLikesCounter(Long postId) {
        String key = POST_KEY_PREFIX + postId + LIKE_KEY_SUFFIX;
        return (Long) cacheRedisTemplate.opsForValue().get(key);
    }

    public Long getCommentsCounter(Long postId) {
        String key = POST_KEY_PREFIX + postId + COMMENT_COUNT_SUFFIX;
        return (Long) cacheRedisTemplate.opsForValue().get(key);
    }
}
