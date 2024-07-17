package faang.school.postservice.service.redis.author;

import faang.school.postservice.model.redis.AuthorRedisCache;

import java.util.concurrent.CompletableFuture;

public interface AuthorRedisCacheService {

    CompletableFuture<AuthorRedisCache> save(AuthorRedisCache entity);
}
