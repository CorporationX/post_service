package faang.school.postservice.service.post;

import faang.school.postservice.model.cache.CommentCacheModel;
import faang.school.postservice.model.cache.PostCacheModel;
import faang.school.postservice.repository.cache.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneOffset;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostCacheService {
    private final PostCacheRepository postCacheRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${app.cache.post.ttl}")
    private long ttl;
    @Value("${app.cache.post.max-comment:3}")
    private int maxComment;
    @Value("${app.cache.post.key-prefix}")
    private String postKeyPrefix;
    @Value("${app.cache.comment.key-prefix}")
    private String commentKeyPrefix;

    public void addComment(CommentCacheModel commentCacheModel, Long postId) {
        postCacheRepository.findById(String.valueOf(postId))
                        .ifPresent(postCacheModel -> addInZSet(postCacheModel, commentCacheModel, postId));
    }

    private void addInZSet(PostCacheModel postCacheModel, CommentCacheModel commentCacheModel, Long postId) {
        String commentKey = getCommentKey(postId);

        long score = commentCacheModel.getCreatedAt().toEpochSecond(ZoneOffset.UTC);

        Boolean added = redisTemplate.opsForZSet().add(commentKey, commentCacheModel, score);
        if (Boolean.TRUE.equals(added)) {
            log.info("Added comment {} to post {}", commentCacheModel.getId(), postId);
        } else {
            log.info("Comment {} already exists in post {} cache", commentCacheModel.getId(), postId);
        }

        redisTemplate.opsForZSet().removeRange(commentKey, 0, -maxComment - 1);

        redisTemplate.expire(commentKey, Duration.ofSeconds(postCacheModel.getTtl()));
    }

    private String getCommentKey(Long postId) {
        return String.format("%s:%d:%s", postKeyPrefix, postId, commentKeyPrefix);
    }
}
