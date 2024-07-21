package faang.school.postservice.redis.cache.service.author;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.redis.cache.entity.AuthorRedisCache;
import faang.school.postservice.redis.cache.repository.AuthorRedisRepository;
import faang.school.postservice.redis.cache.service.RedisOperations;
import faang.school.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@Async("authorsCacheTaskExecutor")
public class AuthorRedisCacheServiceImpl implements AuthorRedisCacheService {

    private final AuthorRedisRepository authorRedisRepository;
    private final RedisOperations redisOperations;
    private final UserService userService;
    private final UserContext userContext;

    @Override
    public CompletableFuture<AuthorRedisCache> save(AuthorRedisCache entity) {

        userContext.setUserId(entity.getId());

        AuthorRedisCache redisUser = userService.getUserAuthorCacheById(entity.getId());

        entity = redisOperations.updateOrSave(authorRedisRepository, redisUser, redisUser.getId());

        log.info("Saved author with id {} to cache: {}", entity.getId(), redisUser);

        return CompletableFuture.completedFuture(redisUser);
    }
}
