package faang.school.postservice.redis.service;

import faang.school.postservice.redis.model.PostCache;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class RedisPostCacheService {
    @Value("{spring.data.redis.post-cache.views}")
    private String postCacheViewsField;
    @Value("{spring.data.redis.post-cache.likes}")
    private String postCacheLikesField;
    @Value("{spring.data.redis.post-cache.comments-in-post:3}")
    private int commentsInPostQuantity;
    @Value("{spring.data.redis.post-cache.key-prefix}")
    private String postCacheKeyPrefix;
    @Qualifier("redisCacheTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheRedisRepository postCacheRedisRepository;

    public RedisPostCacheService(RedisTemplate<String, Object> redisTemplate, PostCacheRedisRepository postCacheRedisRepository) {
        this.redisTemplate = redisTemplate;
        this.postCacheRedisRepository = postCacheRedisRepository;
    }

    public void incrementConcurrentPostViews(Long postId) {
        redisTemplate.opsForHash().increment(createRedisPostKey(postId), postCacheViewsField, 1);
    }

    public void incrementConcurrentPostLikes(Long postId) {
        redisTemplate.opsForHash().increment(createRedisPostKey(postId), postCacheLikesField, 1);
    }

    public void addCommentToPost(Long postId, Long commentId){
        //todo PostCache has to have 3 comments only.
        String cacheKey = generatePostCacheKey(postId);
        redisTemplate.opsForZSet().add(cacheKey, commentId, System.currentTimeMillis());

        Long setSize = redisTemplate.opsForZSet().zCard(cacheKey);
        if (setSize != null && setSize > commentsInPostQuantity) {
            redisTemplate.opsForZSet().removeRange(cacheKey, 0, setSize - commentsInPostQuantity);
        }
    }

    private String createRedisPostKey(Long postId) {
        return postCacheKeyPrefix + postId;
    }
    //TODO need refactoring here
    private String generatePostCacheKey(Long followerId) {
        return postCacheKeyPrefix + followerId;
    }

    public List<PostCache> getPostCacheByIds(List<Long> postIds) {
        Iterable<PostCache> iterable = postCacheRedisRepository.findAllById(postIds);
        return StreamSupport.stream(iterable.spliterator(), false)
                .toList();
    }
}