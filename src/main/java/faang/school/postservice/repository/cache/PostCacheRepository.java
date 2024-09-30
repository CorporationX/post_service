package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostCacheRepository {

    private static final String CACHE_PREFIX = "post:";
    private static final String COMMENT_SUFFIX = ":comments";
    private static final String LIKE_SUFFIX = ":likes";
    private static final String VIEW_SUFFIX = ":views";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cache.post-ttl-seconds}")
    private long timeToLive;

    @Value("${cache.comments.max}")
    private int maxComments;

    public void save(long postId, PostDto post) {
        String key = CACHE_PREFIX + postId;
        redisTemplate.opsForValue()
                .set(key, post, timeToLive, TimeUnit.SECONDS);
    }

    public Optional<PostDto> getPost(long postId) {
        String key = CACHE_PREFIX + postId;
        Object value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(objectMapper.convertValue(value, PostDto.class));
    }

    public void addComment(long postId, CommentDto comment) {
        String key = CACHE_PREFIX + postId + COMMENT_SUFFIX;

        try {
            String serializedComment = objectMapper.writeValueAsString(comment);

            redisTemplate.opsForList().leftPush(key, serializedComment);
            redisTemplate.opsForList().trim(key, 0, maxComments - 1);
            redisTemplate.expire(key, timeToLive, TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new RuntimeException("Comment deserialization error!", e);
        }
    }

    public List<CommentDto> getComments(long postId) {
        String key = CACHE_PREFIX + postId + COMMENT_SUFFIX;
        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
        return deserializeComments(range);
    }

    public void incrementLike(long postId) {
        String key = CACHE_PREFIX + postId + LIKE_SUFFIX;
        redisTemplate.opsForValue().increment(key);
    }

    public void setLike(long postId, long likes) {
        String key = CACHE_PREFIX + postId + LIKE_SUFFIX;
        redisTemplate.opsForValue()
                .set(key, likes, timeToLive, TimeUnit.SECONDS);
    }

    public Long getLikes(long postId) {
        String key = CACHE_PREFIX + postId + LIKE_SUFFIX;
        Object value = redisTemplate.opsForValue().get(key);
        return objectMapper.convertValue(value, Long.class);
    }

    public void incrementView(long postId) {
        String key = CACHE_PREFIX + postId + VIEW_SUFFIX;
        redisTemplate.opsForValue().increment(key);
    }

    public void setViews(long postId, long views) {
        String key = CACHE_PREFIX + postId + VIEW_SUFFIX;
        redisTemplate.opsForValue()
                .set(key, views, timeToLive, TimeUnit.SECONDS);
    }

    public Long getViews(long postId) {
        String key = CACHE_PREFIX + postId + VIEW_SUFFIX;
        Object value = redisTemplate.opsForValue().get(key);
        return objectMapper.convertValue(value, Long.class);
    }

    private List<CommentDto> deserializeComments(List<Object> serializedComments) {
        return serializedComments.stream()
                .map(obj -> {
                    try {
                        return objectMapper.readValue((String) obj, CommentDto.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Comment deserialization error!", e);
                    }
                })
                .collect(Collectors.toList());
    }
}
