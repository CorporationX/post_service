package faang.school.postservice.redis.cache.service.author;

import faang.school.postservice.redis.cache.entity.AuthorRedisCache;

import java.util.concurrent.CompletableFuture;

public interface AuthorRedisCacheService {

    CompletableFuture<AuthorRedisCache> save(AuthorRedisCache entity);
}
