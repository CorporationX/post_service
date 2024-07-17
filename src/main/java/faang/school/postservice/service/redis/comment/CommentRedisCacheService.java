package faang.school.postservice.service.redis.comment;

import faang.school.postservice.model.redis.CommentRedisCache;

import java.util.concurrent.CompletableFuture;

public interface CommentRedisCacheService {

    CompletableFuture<CommentRedisCache> save(CommentRedisCache entity);
}
