package faang.school.postservice.redis.cache.service.author;

import faang.school.postservice.redis.cache.entity.AuthorCache;

import java.util.concurrent.CompletableFuture;

public interface AuthorCacheService {

    CompletableFuture<AuthorCache> save(AuthorCache entity);
}
