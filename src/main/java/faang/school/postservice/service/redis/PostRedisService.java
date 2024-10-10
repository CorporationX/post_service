package faang.school.postservice.service.redis;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.redis.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostRedisService {
    private final PostRedisRepository postRedisRepository;
    private final PostMapper postMapper;
    private final RedisLockRegistry redisLockRegistry;

    @Value("${spring.data.redis.cache.post.comments.max-size}")
    private int commentsMaxSize;
    @Value("${spring.data.redis.lock-registry.try-lock-millis}")
    private long tryLockMillis;
    @Value("${spring.data.redis.cache.post.prefix}")
    private String postPrefix;

    public List<PostRedis> getAllByIds(Iterable<Long> ids) {
        Iterable<PostRedis> postRedisIterable = postRedisRepository.findAllById(ids);
        return StreamSupport.stream(postRedisIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void save(Post post) {
        postRedisRepository.save(postMapper.toRedis(post));
    }

    public void updateIfExists(Post updatedPost) {
        if (updatedPost.isPublished()) {
            if (existsById(updatedPost.getId())) {
                PostRedis postRedis = findById(updatedPost.getId());
                postRedis.setContent(updatedPost.getContent());
                postRedisRepository.save(postMapper.toRedis(updatedPost));
            }
        }
    }

    public void deleteIfExists(Long id) {
        if (existsById(id)) {
            postRedisRepository.deleteById(id);
        }
    }

    public boolean existsById(Long id) {
        return postRedisRepository.existsById(id);
    }

    public PostRedis findById(Long id) {
        return postRedisRepository.findById(id).orElse(null);
    }

    public void addCommentConcurrent(CommentRedis comment) {
        String key = postPrefix + comment.getPostId();
        if (!existsById(comment.getPostId())) {
            log.info("{} not found in cache", key);
            return;
        }
        log.info("Adding comment by id {} to {}", comment.getId(), key);
        Lock lock = redisLockRegistry.obtain(key);
        try {
            if (lock.tryLock(tryLockMillis, TimeUnit.MILLISECONDS)) {
                log.info("Key {} locked for adding comment by id {}", key, comment.getId());
                try {
                    addComment(comment);
                } finally {
                    lock.unlock();
                    log.info("Key {} unlocked after adding comment by id {}", key, comment.getId());
                }
            } else {
                log.warn("Failed to acquire lock for {}", key);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void updateViewsConcurrent(Long postId, Long views) {
        String key = postPrefix + postId;
        if (!existsById(postId)) {
            log.info("{} not found in cache", key);
            return;
        }
        log.info("Updating views for {}", key);
        Lock lock = redisLockRegistry.obtain(key);
        try {
            if (lock.tryLock(tryLockMillis, TimeUnit.MILLISECONDS)) {
                log.info("Key {} locked for updating views", key);
                try {
                    updateViews(postId, views);
                } finally {
                    lock.unlock();
                    log.info("Key {} unlocked after updating views", key);
                }
            } else {
                log.warn("Failed to acquire lock for {}", key);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void updateViews(Long postId, Long views) {
        PostRedis post = findById(postId);
        post.setViews(views);
        postRedisRepository.save(post);
    }

    private void addComment(CommentRedis comment) {
        PostRedis postRedis = findById(comment.getPostId());
        TreeSet<CommentRedis> comments = postRedis.getComments();
        if (comments == null) {
            comments = new TreeSet<>();
        }
        comments.add(comment);
        while (comments.size() > commentsMaxSize) {
            log.info("Removing excess comment from post by id {}", comment.getPostId());
            comments.pollLast();
        }
        postRedis.setComments(comments);
        postRedisRepository.save(postRedis);
    }
}