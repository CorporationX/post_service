package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService {
    private static final String POST_KEY_PREFIX = "post:";

    private final JedisPool jedisPool = new JedisPool();
    private final ObjectMapper objectMapper;

    public void addPostToCash(Post post) {
        log.info("Add to cash post with id: {}", post.getId());
        LocalDateTime publishedAt = post.getPublishedAt();
        long timestamp = publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();
        String json = toJson(post);
        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.zadd("java", timestamp, post.getId() + "");
//            jedis.mget("post:1", "post:3");
            jedis.set(POST_KEY_PREFIX + post.getId(), json);
            post.getHashTags().forEach(tag -> {
                jedis.zadd(tag, timestamp, post.getId() + "");
            });
        }
    }

    public void postCacheProcess(Post updatedPost) {

    }

    public Post findPostById(long id) {
        try (Jedis jedis = jedisPool.getResource()) {
            String primalPostJson = jedis.get(POST_KEY_PREFIX + id);
            return toPost(primalPostJson);
        }
    }

    public void updatePostInCash(List<String> primeHashTags, Post post) {
        log.info("Update in cash post with id: {}", post.getId());
        String json = toJson(post);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(POST_KEY_PREFIX + post.getId(), json);
        }
    }

    public void deletePostInCash(Post post) {

    }

    private String toJson(Post post) {
        log.info("Parse to json post with id: {}", post.getId());
        try {
            return objectMapper.writeValueAsString(post);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Post toPost(String json) {
        log.info("Parse to Post json: {}", json);
        try {
            return objectMapper.readValue(json, Post.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getPrimalHashTagsById(long id) {
        Post primalPost = findPostById(id);
//        if ()
        return null;
    }
}
