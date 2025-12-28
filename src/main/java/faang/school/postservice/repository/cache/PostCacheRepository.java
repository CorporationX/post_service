package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.cache.PostCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostCacheRepository {
    @Value("${redis.cache.post.key-prefix}")
    private String keyPrefix;

    @Value("${redis.cache.post.ttl-days}")
    private int ttlDays;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(PostCacheDto post) {
        String key = keyPrefix + post.id();

        try {
            String json = objectMapper.writeValueAsString(post);
            redisTemplate.opsForValue().set(key, json, ttlDays, TimeUnit.DAYS);
            log.debug("Post {} added to cache", post.id());
        } catch (Exception e) {
            log.error("Failed add post to cache {}", post, e);
        }
    }

    private String likesKey(Long postId) {
        return keyPrefix + postId + ":likes";
    }

    public Long incrementLikeCount(Long postId) {
        String key = likesKey(postId);

        Long result = redisTemplate.opsForValue().increment(key);

        if (result != null && result == 1L) {
            redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
        }

        return result;
    }

    private String commentsKey(Long postId) {
        return keyPrefix + postId + ":comments";
    }

    public Long incrementCommentCount(Long postId) {
        String key = commentsKey(postId);
        Long result = redisTemplate.opsForValue().increment(key);
        if (result != null && result == 1L) {
            redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
        }
        return result;
    }

    public Optional<PostCacheDto> findById(Long postId) {
        String key = keyPrefix + postId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            PostCacheDto postCacheDto = objectMapper.readValue(json, PostCacheDto.class);
            long likesCount = getLikesCount(postId);
            long commentsCount = getCommentsCount(postId);
            return Optional.of(getPostWithCounters(postCacheDto, likesCount, commentsCount));
        } catch (Exception e) {
            log.error("Failed to deserialize post {}", postId, e);
            return Optional.empty();
        }
    }

    private long getLikesCount(Long postId) {
        String likesCount = redisTemplate.opsForValue().get(likesKey(postId));
        return likesCount == null ? 0L : Long.parseLong(likesCount);
    }

    private long getCommentsCount(Long postId) {
        String commentsCount = redisTemplate.opsForValue().get(commentsKey(postId));
        return commentsCount == null ? 0L : Long.parseLong(commentsCount);
    }

    private PostCacheDto getPostWithCounters(PostCacheDto postCacheDto, long likesCount, long commentsCount) {
        return PostCacheDto.builder()
                .id(postCacheDto.id())
                .authorId(postCacheDto.authorId())
                .content(postCacheDto.content())
                .createdAt(postCacheDto.createdAt())
                .likeCount(likesCount)
                .commentCount(commentsCount)
                .build();
    }
}