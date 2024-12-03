package faang.school.postservice.repository.cache.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.cache.post.PostCacheDto;
import faang.school.postservice.dto.comment.CommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCacheRepositoryImpl implements PostCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private final RedisProperties redisProperties;

    @Override
    public void save(PostCacheDto postCacheDto) {
        redisTemplate.opsForValue().set(String.valueOf(postCacheDto.getPostId()), postCacheDto);
        log.info("save post with id: {} in Redis", postCacheDto.getPostId());
    }

    @Override
    public boolean incrementLikesCount(Long postId) {
        String lockKey = "lock:Post:" + postId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) {
                try {
                    String key = String.valueOf(postId);
                    Object postObject = redisTemplate.opsForValue().get(key);
                    if (postObject == null) {
                        log.error("Post with id {} not found in Redis", postId);
                        throw new IllegalArgumentException("Post not found in cache");
                    }
                    PostCacheDto postCacheDto = objectMapper.convertValue(postObject,
                            PostCacheDto.class);
                    postCacheDto.setLikesCount(postCacheDto.getLikesCount() + 1);
                    redisTemplate.opsForValue().set(key, postCacheDto);
                    log.debug("Incremented likesCount for post with id: {} to {}",
                            postId, postCacheDto.getLikesCount());
                    return true;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    @Override
    public Optional<PostCacheDto> findById(Long postId) {
        String key = String.valueOf(postId);
        try {
            Object postObject = redisTemplate.opsForValue().get(key);
            PostCacheDto postCacheDto = objectMapper.convertValue(postObject, PostCacheDto.class);
            return Optional.of(postCacheDto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean updatePostsComments(Long postId, CommentDto commentDto) {
        log.debug("Starting to insert commentDto into postCacheDto");
        try {
            Optional<PostCacheDto> postCacheDtoOptional = findById(postId);
            if (postCacheDtoOptional.isPresent()) {
                PostCacheDto postCacheDto = postCacheDtoOptional.get();
                Deque<CommentDto> commentsDeque = postCacheDto.getComments() != null
                        ? new LinkedList<>(postCacheDto.getComments())
                        : new LinkedList<>();
                commentsDeque.addFirst(commentDto);
                log.debug("Redis commentsSize property: {}", redisProperties.getCommentsSize());
                if (commentsDeque.size() >= redisProperties.getCommentsSize()) {
                    commentsDeque.removeLast();
                }
                postCacheDto.setComments(new ArrayList<>(commentsDeque));
                save(postCacheDto);
                log.debug("Updated postCacheDto: {}", postCacheDto);
            } else {
                log.warn("PostCacheDto not found for postId: {}", postId);
            }
        } catch (Exception e) {
            log.error("Exception occurred while inserting comment into post cache dto", e);
            return false;
        }
        log.debug("Successfully added comment to post in cache");
        return true;
    }
}
