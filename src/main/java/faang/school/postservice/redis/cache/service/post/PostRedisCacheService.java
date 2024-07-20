package faang.school.postservice.redis.cache.service.post;

import faang.school.postservice.redis.cache.entity.PostRedisCache;

import java.util.concurrent.CompletableFuture;

public interface PostRedisCacheService {

    CompletableFuture<PostRedisCache> save(PostRedisCache entity);

    void incrementLikes(long postId);

    void decrementLikes(long postId);

    void deleteById(long postId);
}
