package faang.school.postservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feed.CommentRedisEvent;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.exception.JsonDeserializationException;
import faang.school.postservice.exception.JsonSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
@CacheConfig(cacheNames = "posts")
@RequiredArgsConstructor
public class PostRedisRepository {

    private static final String KEY_PREFIX = "post:";
    private static final String COMMENT_POSTFIX = ":comments";
    private static final String LIKE_POSTFIX = ":likes";
    private static final String VIEW_POSTFIX = ":views";

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Boolean> addAndTrimZSetScript;

    @Value("${spring.data.redis.object-cache-options.comments-count}")
    private int commentsCount;

    @CachePut(key = "#postDto.id")
    public PostRedisDto savePost(PostRedisDto postDto) {
        return postDto;
    }

    public void savePostsBatch(List<PostRedisDto> posts) {
        Map<String, PostRedisDto> keyValueMap = posts.stream()
                .collect(Collectors.toMap(post -> "posts::" + post.id(), Function.identity()));
        redisTemplate.opsForValue().multiSet(keyValueMap);
    }

    public Map<Long, PostRedisDto> findByIds(List<Long> postIds) {
        List<String> keys = postIds.stream()
                .map(id -> "posts::" + id)
                .toList();

        List<Object> cachedObjects = redisTemplate.opsForValue().multiGet(keys);

        Map<Long, PostRedisDto> result = new LinkedHashMap<>();
        if (cachedObjects != null) {
            int size = Math.min(postIds.size(), cachedObjects.size());
            for (int i = 0; i < size; i++) {
                Object cachedObject = cachedObjects.get(i);
                if (cachedObject instanceof PostRedisDto dto) {
                    result.put(postIds.get(i), dto);
                }
            }
        }

        return result;
    }

    public void addComment(CommentRedisEvent commentDto) {
        String key = KEY_PREFIX + commentDto.postId() + COMMENT_POSTFIX;
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

    public List<CommentRedisEvent> getComments(Long postId) {
        String key = KEY_PREFIX + postId + COMMENT_POSTFIX;
        Set<Object> raw = redisTemplate.opsForZSet().range(key, 0, -1);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyList();
        }
        return raw.stream()
                .map(object -> deserialize(object.toString()))
                .toList();
    }

    public Map<Long, List<CommentRedisEvent>> getCommentsByIds(List<Long> postIds) {
        Map<Long, List<CommentRedisEvent>> result = new HashMap<>();
        postIds.forEach(id -> result.put(id, getComments(id)));

        return result;
    }

    public void incrementLikes(Long postId) {
        redisTemplate.opsForValue().increment(KEY_PREFIX + postId + LIKE_POSTFIX);
    }

    public void decrementLikes(Long postId) {
        redisTemplate.opsForValue().decrement(KEY_PREFIX + postId + LIKE_POSTFIX);
    }

    public Map<Long, Integer> getLikesByIds(List<Long> postIds) {
        List<Object> cachedObjects = findObjectsByIds(postIds, LIKE_POSTFIX);

        Map<Long, Integer> result = new LinkedHashMap<>();
        if (cachedObjects != null) {
            int size = Math.min(postIds.size(), cachedObjects.size());
            for (int i = 0; i < size; i++) {
                result.put(postIds.get(i), Integer.parseInt(cachedObjects.get(i).toString()));
            }
        }

        return result;
    }

    public void addPostLikesBatch(Map<Long, Integer> likes) {
        Map<String, Integer> keyValueMap = likes.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> KEY_PREFIX + e.getKey() + LIKE_POSTFIX,
                        Map.Entry::getValue)
                );
        redisTemplate.opsForValue().multiSet(keyValueMap);
    }

    public void incrementViews(Long postId) {
        redisTemplate.opsForValue().increment(KEY_PREFIX + postId + VIEW_POSTFIX);
    }

    public Map<Long, Long> getViewsByIds(List<Long> postIds) {
        List<Object> cachedObjects = findObjectsByIds(postIds, VIEW_POSTFIX);

        Map<Long, Long> result = new LinkedHashMap<>();
        if (cachedObjects != null) {
            int size = Math.min(postIds.size(), cachedObjects.size());
            for (int i = 0; i < size; i++) {
                result.put(postIds.get(i), Long.parseLong(cachedObjects.get(i).toString()));
            }
        }

        return result;
    }

    public Map<Long, Long> getAllViewCounts() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*" + VIEW_POSTFIX);
        Map<Long, Long> result = new HashMap<>();

        keys.forEach(key -> {
            Long postId = Long.valueOf(key.replace(KEY_PREFIX, "").replace(VIEW_POSTFIX, ""));
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                result.put(postId, Long.valueOf((String) value));
            }
        });

        return result;
    }

    public void addPostViewsBatch(Map<Long, Long> views) {
        Map<String, Long> keyValueMap = views.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> KEY_PREFIX + entry.getKey() + VIEW_POSTFIX,
                        Map.Entry::getValue)
                );
        redisTemplate.opsForValue().multiSet(keyValueMap);
    }

    private String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException("Serialization object %s in json error", object.toString());
        }
    }

    private CommentRedisEvent deserialize(String json) {
        try {
            return objectMapper.readValue(json, CommentRedisEvent.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationException("Deserialization json %s to comment object error", json);
        }
    }

    private List<Object> findObjectsByIds(List<Long> ids, String postKey) {
        List<String> keys = ids.stream()
                .map(id -> KEY_PREFIX + id + postKey)
                .toList();

        return redisTemplate.opsForValue().multiGet(keys);
    }
}
