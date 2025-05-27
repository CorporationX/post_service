package faang.school.postservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feed.CommentAddedEvent;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.exception.JsonDeserializationException;
import faang.school.postservice.exception.JsonSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@CacheConfig(cacheNames = "posts")
@RequiredArgsConstructor
public class PostRedisRepository {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Boolean> addAndTrimZSetScript;

    @Value("${spring.data.redis.object-cache-options.comments-count}")
    private int commentsCount;

    @CachePut(key = "#postDto.id")
    public PostRedisDto savePost(PostRedisDto postDto) {
        return postDto;
    }

    @Cacheable(key = "#postId", unless = "#result == null")
    public PostRedisDto findById(Long postId) {
        log.warn("Not found cache with post id {}", postId);
        return null;
    }

    public void addComment(CommentAddedEvent commentDto) {
        String key = "post:" + commentDto.postId() + ":comments";
        String value = serialize(commentDto);
        long score = commentDto.createdAt().toInstant(ZoneOffset.UTC).getEpochSecond();

        redisTemplate.execute(
                addAndTrimZSetScript,
                Collections.singletonList(key),
                value,
                String.valueOf(score),
                String.valueOf(commentsCount)
        );
    }

    public List<CommentAddedEvent> getComments(Long postId) {
        String key = "post:" + postId + ":comments";
        Set<Object> raw = redisTemplate.opsForZSet().range(key, 0, -1);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyList();
        }
        return raw.stream()
                .map(object -> deserialize(object.toString()))
                .toList();
    }

    public void incrementLikes(Long postId) {
        redisTemplate.opsForValue().increment("post:" + postId + ":likes");
    }

    public void decrementLikes(Long postId) {
        redisTemplate.opsForValue().decrement("post:" + postId + ":likes");
    }

    public Long getLikes(Long postId) {
        Object value = redisTemplate.opsForValue().get("post:" + postId + ":likes");
        return parseValue(value);
    }

    public void incrementViews(Long postId) {
        redisTemplate.opsForValue().increment("post:" + postId + ":views");
    }

    public Long getViews(Long postId) {
        Object value = redisTemplate.opsForValue().get("post:" + postId + ":views");
        return parseValue(value);
    }

    private Long parseValue(Object value) {
        return value == null
                ? 0L
                : Long.parseLong(value.toString());
    }

    private String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException("Serialization object %s in json error", object.toString());
        }
    }

    private CommentAddedEvent deserialize(String json) {
        try {
            return objectMapper.readValue(json, CommentAddedEvent.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationException("Deserialization json %s to comment object error", json);
        }
    }
}
