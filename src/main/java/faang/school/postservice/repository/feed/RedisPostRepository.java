package faang.school.postservice.repository.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisPostRepository {
    private static final String POST_KEY_PREFIX = "post:";
    private static final String COMMENT_KEY_SUFFIX = ":comments";
    private static final String LIKE_KEY_SUFFIX = ":likes";
    private static final String COMMENT_COUNT_SUFFIX = ":commentCount";
    private final RedisTemplate<String, Object> cacheRedisTemplate;

    @Value("${spring.data.redis.cache.ttl.post}")
    private long ttl;
    @Value("${data.redis.cache.feed.showLastComments}")
    private int showLastComments;

    public void savePost(PostDto postDto) {
        String key = POST_KEY_PREFIX + postDto.getId();
        cacheRedisTemplate.opsForValue().set(key, postDto, Duration.ofSeconds(ttl));
    }

    public PostDto getPost(Long postId) {
        String key = POST_KEY_PREFIX + postId;
        return (PostDto) cacheRedisTemplate.opsForValue().get(key);
    }

    public void deletePost(Long postId) {
        String key = POST_KEY_PREFIX + postId;
        cacheRedisTemplate.delete(key);
    }

    public void addComment(Long postId, CommentDto commentDto) {
        String key = POST_KEY_PREFIX + postId + COMMENT_KEY_SUFFIX;

        cacheRedisTemplate.opsForZSet().add(key,commentDto, System.currentTimeMillis());
        cacheRedisTemplate.opsForZSet().removeRange(key, 0, -showLastComments - 1);
        cacheRedisTemplate.expire(key, Duration.ofSeconds(ttl));
    }

    public List<CommentDto> getComments(Long postId) {
        String key = POST_KEY_PREFIX + postId + COMMENT_KEY_SUFFIX;
        Set<Object> comments = cacheRedisTemplate.opsForZSet().reverseRange(key, 0, -1);
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments.stream()
                .map(comment -> (CommentDto) comment)
                .toList();
    }

    public void deleteComments(long postId) {
        String key = POST_KEY_PREFIX + postId + COMMENT_KEY_SUFFIX;
        cacheRedisTemplate.delete(key);
    }

    public void deleteComment(Long postId, Long commentId) {
        String key = POST_KEY_PREFIX + postId + COMMENT_KEY_SUFFIX;
        List<CommentDto> comments = getComments(postId);

        boolean removed = comments.removeIf(comment -> comment.getId() == commentId);

        if (removed) {
            cacheRedisTemplate.delete(key);
            for (int i = comments.size() - 1; i >= 0; i--) {
                cacheRedisTemplate.opsForList().leftPush(key, comments.get(i));
            }
            cacheRedisTemplate.expire(key, Duration.ofSeconds(ttl));
        }
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
}
