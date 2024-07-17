package faang.school.postservice.service.redis.author;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.redis.AuthorRedisCache;
import faang.school.postservice.property.CacheProperty;
import faang.school.postservice.repository.redis.AuthorRedisRepository;
import faang.school.postservice.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorRedisCacheServiceImpl implements AuthorRedisCacheService {

    @Value("${spring.cache.cache-settings.users.name}")
    private String cacheName;

    private int ttl;
    private final CacheProperty cacheProperty;
    private final AuthorRedisRepository authorRedisRepository;
    private final UserService userService;
    private final UserContext userContext;

    @PostConstruct
    public void init() {
        ttl = cacheProperty.getCacheSettings().get(cacheName).getTtl();
    }

    @Override
    @Async("cacheTaskExecutor")
    @Retryable(retryFor = { OptimisticEntityLockException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public CompletableFuture<AuthorRedisCache> save(AuthorRedisCache entity) {

        userContext.setUserId(entity.getId());

        AuthorRedisCache redisUser = userService.getUserAuthorCacheById(entity.getId());

        redisUser.setTtl(ttl);
        entity = updateOrSave(redisUser);

        log.info("Saved author with id {} to cache: {}", entity.getId(), redisUser);

        return CompletableFuture.completedFuture(redisUser);
    }

    private AuthorRedisCache updateOrSave(AuthorRedisCache entity) {

        authorRedisRepository.findById(entity.getId()).ifPresent(authorRedisRepository::delete);
        return authorRedisRepository.save(entity);
    }
}
