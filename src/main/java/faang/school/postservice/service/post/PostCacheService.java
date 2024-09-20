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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService {
    private static final String POST_KEY_PREFIX = "post:";

    private final JedisPool jedisPool = new JedisPool();
    private final ObjectMapper objectMapper;
    private final PostMapper postMapper;

    public void newPostProcess(Post post) {
        log.info("Add to cash post with id: {}", post.getId());
        savePost(post);
        saveNewHashTags(post.getHashTags(), post.getId(), post.getPublishedAt());
    }

    public void updatePostProcess(Post updatedPost) {
        log.info("Updated post with id: {} in cache process", updatedPost.getId());
        Optional<Post> primalPost = findPostById(updatedPost.getId());
        List<String> primalHashTags;
        if (primalPost.isPresent()) {
            deletePostById(updatedPost.getId());
            primalHashTags = primalPost.get().getHashTags();
        } else {
            primalHashTags = new ArrayList<>();
        }
        List<String> updatedHashTags = updatedPost.getHashTags();
        List<String> deletedHashTags = getDeletedHashTags(primalHashTags, updatedHashTags);
        List<String> newHashTags = getNewHashTags(primalHashTags, updatedHashTags);

        if (!deletedHashTags.isEmpty()) {
            deleteCachedHashTags(deletedHashTags, updatedPost.getId());
        }
        if (!newHashTags.isEmpty()) {
            saveNewHashTags(newHashTags, updatedPost.getId(), updatedPost.getPublishedAt());
        }

        if (!updatedPost.getHashTags().isEmpty()) {
            savePost(updatedPost);
        }
    }

    public void deletePostProcess(Post deletedPost) {
        findPostById(deletedPost.getId()).ifPresent(post -> {
            deletePostById(post.getId());
            deleteCachedHashTags(post.getHashTags(), post.getId());
        });
    }

    private void deletePostById(long postId) {
        log.info("Delete from cache post with id: {}", postId);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(POST_KEY_PREFIX + postId);
        }
    }

    private void savePost(Post post) {
        log.info("Save to cache post with id: {}", post.getId());
        String json = toJson(post);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(POST_KEY_PREFIX + post.getId(), json);
        }
    }

    private void deleteCachedHashTags(List<String> deletedHashTags, long postId) {
        log.info("Delete hash-tags from cache: {}", deletedHashTags);
        try (Jedis jedis = jedisPool.getResource()) {
            deletedHashTags.forEach(tag -> jedis.zrem(tag, postId + ""));
        }
    }

    private void saveNewHashTags(List<String> newHashTags, long postId, LocalDateTime publishedAt) {
        log.info("Save new hash-tags to cache: {}", newHashTags);
        long timestamp = getTimeStamp(publishedAt);
        try (Jedis jedis = jedisPool.getResource()) {
            newHashTags.forEach(tag -> jedis.zadd(tag, timestamp, postId + ""));
        }
    }

    private long getTimeStamp(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    private List<String> getNewHashTags(List<String> primalHashTags, List<String> updatedHashTags) {
        log.info("Get new hash-tags between {} AND {}", primalHashTags, updatedHashTags);
        List<String> newHashTags = new ArrayList<>(updatedHashTags);
        newHashTags.removeAll(primalHashTags);
        return newHashTags;
    }

    private List<String> getDeletedHashTags(List<String> primalHashTags, List<String> updatedHashTags) {
        log.info("Get deleted hash-tags between {} AND {}", primalHashTags, updatedHashTags);
        List<String> deletedHashTags = new ArrayList<>(primalHashTags);
        deletedHashTags.removeAll(updatedHashTags);
        return deletedHashTags;
    }

    public Optional<Post> findPostById(long id) {
        log.info("Finding in cache post with id: {}", id);
        try (Jedis jedis = jedisPool.getResource()) {
            String primalPostJson = jedis.get(POST_KEY_PREFIX + id);
            if (primalPostJson == null) {
                return Optional.empty();
            }
            return Optional.of(toPost(primalPostJson));
        }
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

    private Post toPost(String json) {
        log.info("Parse to Post json: {}", json);
        try {
            return objectMapper.readValue(json, Post.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
