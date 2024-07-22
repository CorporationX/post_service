package faang.school.postservice.redis.cache.service.author;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.redis.cache.entity.AuthorCache;
import faang.school.postservice.redis.cache.repository.AuthorCacheRepository;
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
public class AuthorCacheServiceImpl implements AuthorCacheService {

    private final AuthorCacheRepository authorCacheRepository;
    private final RedisOperations redisOperations;
    private final UserService userService;
    private final UserContext userContext;

    @Override
    public CompletableFuture<AuthorCache> save(AuthorCache entity) {

        userContext.setUserId(entity.getId());

        AuthorCache redisUser = userService.getUserAuthorCacheById(entity.getId());

        entity = redisOperations.updateOrSave(authorCacheRepository, redisUser, redisUser.getId());

        log.info("Saved author with id {} to cache: {}", entity.getId(), redisUser);

        return CompletableFuture.completedFuture(redisUser);
    }
}
