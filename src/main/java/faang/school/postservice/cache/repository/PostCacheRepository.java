package faang.school.postservice.cache.repository;

import faang.school.postservice.cache.entity.PostCache;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostCacheRepository {
    @Value("${spring.data.redis.cache.post.key}")
    private String postCacheKey;

    @Value("${spring.data.redis.cache.post.likes-lock}")
    private String postCacheLikesLockKey;

    @Value("${spring.data.redis.cache.post.views-lock}")
    private String postCacheViewsLockKey;
    private final PostRepository postRepository;
    private final RedisTemplate<String, PostCache> postCacheRedisTemplate;
    private final RedissonClient redissonClient;

    @Transactional
    public List<PostCache> findPostsByIds(Collection<Long> postIds) {
        List<String> keys = postIds.stream()
                .map(id -> postCacheKey.formatted(id))
                .toList();

        List<PostCache> postCaches = postCacheRedisTemplate.opsForValue().multiGet(keys);

        return postCaches != null ? postCaches.stream()
                .filter(Objects::nonNull)
                .toList() : Collections.emptyList();
    }

    @Transactional
    public PostCache findById(Long postId) {
        PostCache postCache = postCacheRedisTemplate.opsForValue().get(postCacheKey.formatted(postId));
        if (postCache == null) {
            Post post = postRepository.findById(postId).orElseThrow(() -> {
                String errMessage = "Post with ID: %d was not found".formatted(postId);
                log.error(errMessage);
                return new EntityNotFoundException(errMessage);
            });

            PostCache newPostCache = PostCache.builder()
                    .authorId(post.getAuthorId())
                    .content(post.getContent())
                    .likeCount(post.getLikes().size())
                    .build();
            save(newPostCache);
        }
        return postCache;
    }

    @Transactional
    public void save(PostCache postCache) {
        postCacheRedisTemplate.opsForValue()
                .set(postCacheKey.formatted(postCache.getId()), postCache);
    }

    @Transactional
    public void addLikeToPost(long postId) {
        String postKey = postCacheKey.formatted(postId);
        RLock lock = redissonClient.getLock(postCacheLikesLockKey.formatted(postId));

        try {
            lock.lock();
            PostCache postCache = postCacheRedisTemplate.opsForValue().get(postKey);
            if(postCache != null){
                postCache.incrementLikes();
                postCacheRedisTemplate.opsForValue().set(postCacheKey, postCache);
            }
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void addViewToPost(long postId) {
        String postKey = postCacheKey.formatted(postId);
        RLock lock = redissonClient.getLock(postCacheViewsLockKey.formatted(postId));

        try {
            lock.lock();
            PostCache postCache = postCacheRedisTemplate.opsForValue().get(postKey);
            if(postCache != null){
                postCache.incrementViews();
                postCacheRedisTemplate.opsForValue().set(postCacheKey, postCache);
            }
        } finally {
            lock.unlock();
        }
    }
}
