package faang.school.postservice.cache.comment;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class PostCommentCacheImpl implements PostCommentCache {
    private static final String KEY_POST_COMMENT_PATTERN = "post:%s:comments";
    private static final String KEY_COMMENT_PATTERN = "comments:%s";

    @Value("${spring.data.redis.cache.comments.max}")
    private int max;
    @Value("${spring.data.redis.cache.comments.ttl}")
    private int ttl;

    private final RedisTemplate<String, CommentDto> cache;
    private final StringRedisTemplate zsetCache;

    private String getPostCommentsKey(long postId) {
        return String.format(KEY_POST_COMMENT_PATTERN, postId);
    }

    private String getCommentKey(String commentId) {
        return String.format(KEY_COMMENT_PATTERN, commentId);
    }

    @Override
    public void add(CommentDto comment) {
        String postCommentsKey = getPostCommentsKey(comment.postId());
        Double exists = cache.opsForZSet().score(postCommentsKey, comment.id());
        if (exists != null) {
            return;
        }

        String commentKey = getCommentKey(String.valueOf(comment.id()));
        long score = comment.createdAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        zsetCache.opsForZSet().add(postCommentsKey, String.valueOf(comment.id()), score);
        cache.opsForValue().set(commentKey, comment);
        Long size = cache.opsForZSet().size(postCommentsKey);

        if (size != null && size > max) {
            long difference = size - max;
            Set<String> toDelete = zsetCache.opsForZSet().range(postCommentsKey, 0, difference - 1);
            if (toDelete != null) {
                List<String> ids = new ArrayList<>();
                for (String id : toDelete) {
                    ids.add(getCommentKey(id));
                }
                cache.delete(ids);
            }
            cache.opsForZSet().removeRange(postCommentsKey, 0, difference - 1);
        }
    }

    @Override
    public List<CommentDto> getPostComments(long postId) {
        String key = getPostCommentsKey(postId);
        Set<String> ids = zsetCache.opsForZSet().reverseRange(key, 0, max - 1);
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> keys = ids.stream().map(this::getCommentKey).toList();
        List<CommentDto> comments = cache.opsForValue().multiGet(keys);
        if (Objects.isNull(comments)) {
            return Collections.emptyList();
        }
        return comments.stream().filter(Objects::nonNull).toList();
    }
}