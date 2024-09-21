package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService {
    private static final String POST_ID_PREFIX = "post:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> zSetOperations;

    public void newPostProcess(PostCacheDto post) {
        log.info("New post process, post with id: {}", post.getId());
        List<String> newTags = post.getHashTags();
        if (!newTags.isEmpty()) {
            String postId = POST_ID_PREFIX + post.getId();
            long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            addPostToCache(post, postId, newTags, timestamp);
        }
    }

    public void deletePostProcess(PostCacheDto post, List<String> primalTags) {
        log.info("Delete post process, post with id: {}", post.getId());
        if (!primalTags.isEmpty()) {
            String postId = POST_ID_PREFIX + post.getId();
            deletePostOfCache(postId, primalTags);
        }
    }

    public void updatePostProcess(PostCacheDto post, List<String> primalTags) {
        log.info("Updated post process, post with id: {}", post.getId());
        List<String> updTags = post.getHashTags();
        String postId = POST_ID_PREFIX + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        if (primalTags.isEmpty() && !updTags.isEmpty()) {
            addPostToCache(post, postId, updTags, timestamp);
        } else if (!primalTags.isEmpty() && updTags.isEmpty()) {
            deletePostOfCache(postId, primalTags);
        } else if (!primalTags.isEmpty()) {
            updatePostOfCache(post, postId, primalTags, updTags, timestamp);
        }
    }

    private void addPostToCache(PostCacheDto post, String postId, List<String> newTags, long timestamp) {
        log.info("Add post to cache, post with id: {}", postId);
        redisTemplate.watch(postId);
        newTags.forEach(redisTemplate::watch);
        boolean success = false;
        while (!success) {
            redisTemplate.setEnableTransactionSupport(true);
            redisTemplate.multi();

            redisTemplate.opsForValue().set(postId, post);
            newTags.forEach(tag -> zSetOperations.add(tag, postId, timestamp));

            List<Object> result = redisTemplate.exec();
            if (!result.isEmpty()) {
                success = true;
                redisTemplate.setEnableTransactionSupport(false);
            } else {
                redisTemplate.discard();
            }
        }
        redisTemplate.unwatch();
    }

    private void deletePostOfCache(String postId, List<String> primalTags) {
        log.info("Delete post of cache, post with id: {}", postId);
        redisTemplate.watch(postId);
        primalTags.forEach(redisTemplate::watch);
        boolean success = false;
        while (!success) {
            redisTemplate.setEnableTransactionSupport(true);
            redisTemplate.multi();

            redisTemplate.delete(postId);
            primalTags.forEach(tag -> zSetOperations.remove(tag, postId));

            List<Object> result = redisTemplate.exec();
            if (!result.isEmpty()) {
                success = true;
                redisTemplate.setEnableTransactionSupport(false);
            } else {
                redisTemplate.discard();
            }
        }
        redisTemplate.unwatch();
    }

    private void updatePostOfCache(PostCacheDto post, String postId, List<String> primalTags, List<String> updTags,
                                   long timestamp) {
        log.info("Update post of cache, post with id: {}", postId);
        List<String> delTags = getDeletedHashTags(primalTags, updTags);
        List<String> newTags = getNewHashTags(primalTags, updTags);
        redisTemplate.watch(postId);
        delTags.forEach(redisTemplate::watch);
        newTags.forEach(redisTemplate::watch);
        boolean success = false;
        while (!success) {
            redisTemplate.setEnableTransactionSupport(true);
            redisTemplate.multi();

            redisTemplate.opsForValue().set(postId, post);
            delTags.forEach(tag -> zSetOperations.remove(tag, postId));
            newTags.forEach(tag -> zSetOperations.add(tag, postId, timestamp));

            List<Object> result = redisTemplate.exec();
            if (!result.isEmpty()) {
                success = true;
                redisTemplate.setEnableTransactionSupport(false);
            } else {
                redisTemplate.discard();
            }
        }
        redisTemplate.unwatch();
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
}
