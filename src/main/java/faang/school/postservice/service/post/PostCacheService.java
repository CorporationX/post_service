package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.serializable.PostJsonDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService {
    private static final String POST_ID_PREFIX = "post:";

    private final JedisPool jedisPool = new JedisPool();
    private final ObjectMapper objectMapper;
    private final PostMapper postMapper;

    public void newPostProcess(Post post) {
        log.info("New post process, post with id: {}", post.getId());
        List<String> newTags = post.getHashTags();
        if (!newTags.isEmpty()) {
            String postId = POST_ID_PREFIX + post.getId();
            String postJson = toJson(post);
            long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            try (Jedis jedis = jedisPool.getResource()) {
                addPostToCache(postId, newTags, jedis, postJson, timestamp);
            }
        }
    }

    public void deletePostProcess(Post post, List<String> primalTags) {
        log.info("Delete post process, post with id: {}", post.getId());
        if (!primalTags.isEmpty()) {
            String postId = POST_ID_PREFIX + post.getId();
            try (Jedis jedis = jedisPool.getResource()) {
                deletePostOfCache(postId, primalTags, jedis);
            }
        }
    }

    public void updatePostProcess(Post post, List<String> primalTags) {
        log.info("Updated post process, post with id: {}", post.getId());
        List<String> updTags = post.getHashTags();
        String postId = POST_ID_PREFIX + post.getId();
        String postJson = toJson(post);
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        try (Jedis jedis = jedisPool.getResource()) {
            if (primalTags.isEmpty() && !updTags.isEmpty()) {
                addPostToCache(postId, updTags, jedis, postJson, timestamp);
            } else if (!primalTags.isEmpty() && updTags.isEmpty()) {
                deletePostOfCache(postId, primalTags, jedis);
            } else if (!primalTags.isEmpty()) {
                updatePostOfCache(postId, primalTags, updTags, jedis, postJson, timestamp);
            }
        }
    }

    private void addPostToCache(String postId, List<String> updTags, Jedis jedis, String postJson, long timestamp) {
        log.info("Add post to cache, post with id: {}", postId);
        boolean success = false;
        while (!success) {
            jedis.watch(postId);
            updTags.forEach(jedis::watch);
            Transaction transaction = jedis.multi();

            transaction.set(postId, postJson);
            updTags.forEach(tag -> transaction.zadd(tag, timestamp, postId));

            List<Object> result = transaction.exec();
            if (result != null) {
                success = true;
            }
            jedis.unwatch();
        }
    }

    private void deletePostOfCache(String postId, List<String> primalTags, Jedis jedis) {
        log.info("Delete post of cache, post with id: {}", postId);
        boolean success = false;
        while (!success) {
            jedis.watch(postId);
            primalTags.forEach(jedis::watch);
            Transaction transaction = jedis.multi();

            transaction.del(postId);
            primalTags.forEach(tag -> transaction.zrem(tag, postId));

            List<Object> result = transaction.exec();
            if (result != null) {
                success = true;
            }
            jedis.unwatch();
        }
    }

    private void updatePostOfCache(String postId, List<String> primalTags, List<String> updTags, Jedis jedis,
                                   String postJson, long timestamp) {
        log.info("Update post of cache, post with id: {}", postId);
        List<String> delTags = getDeletedHashTags(primalTags, updTags);
        List<String> newTags = getNewHashTags(primalTags, updTags);
        boolean success = false;
        while (!success) {
            jedis.watch(postId);
            delTags.forEach(jedis::watch);
            newTags.forEach(jedis::watch);
            Transaction transaction = jedis.multi();

            transaction.set(postId, postJson);
            delTags.forEach(tag -> transaction.zrem(tag, postId));
            newTags.forEach(tag -> transaction.zadd(tag, timestamp, postId));

            List<Object> result = transaction.exec();
            if (result != null) {
                success = true;
            }
            jedis.unwatch();
        }
    }

    private List<String> getNewHashTags(List<String> primalHashTags, List<String> updatedHashTags) {
        log.info("Get new hash-tags between primal: {} AND updated: {}", primalHashTags, updatedHashTags);
        List<String> newHashTags = new ArrayList<>(updatedHashTags);
        newHashTags.removeAll(primalHashTags);
        return newHashTags;
    }

    private List<String> getDeletedHashTags(List<String> primalHashTags, List<String> updatedHashTags) {
        log.info("Get deleted hash-tags between primal: {} AND updated: {}", primalHashTags, updatedHashTags);
        List<String> deletedHashTags = new ArrayList<>(primalHashTags);
        deletedHashTags.removeAll(updatedHashTags);
        return deletedHashTags;
    }

    private String toJson(Post post) {
        log.info("Parse to json post with id: {}", post.getId());
        PostJsonDto postJsonDto = postMapper.toPostJsonDto(post);
        try {
            return objectMapper.writeValueAsString(postJsonDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private PostJsonDto toPostJsonDto(String json) {
        log.info("Parse to PostJsonDto json: {}", json);
        try {
            return objectMapper.readValue(json, PostJsonDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
