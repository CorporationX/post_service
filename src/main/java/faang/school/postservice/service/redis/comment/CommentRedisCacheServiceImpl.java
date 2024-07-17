package faang.school.postservice.service.redis.comment;

import faang.school.postservice.model.redis.CommentRedisCache;
import faang.school.postservice.property.CacheProperty;
import faang.school.postservice.repository.redis.CommentRedisRepository;
import faang.school.postservice.service.redis.post.PostRedisCacheService;
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
public class CommentRedisCacheServiceImpl implements CommentRedisCacheService {

    @Value("${spring.cache.cache-settings.comments.name}")
    private String cacheName;

    private int ttl;
    private final CacheProperty cacheProperty;
    private final CommentRedisRepository commentRedisRepository;
    private final PostRedisCacheService postRedisCacheService;

    @PostConstruct
    public void init() {
        ttl = cacheProperty.getCacheSettings().get(cacheName).getTtl();
    }

    @Override
    @Async("cacheTaskExecutor")
    @Retryable(retryFor = {OptimisticEntityLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public CompletableFuture<CommentRedisCache> save(CommentRedisCache entity) {

        entity.setTtl(ttl);
        entity = updateOrSave(entity);

        postRedisCacheService.addCommentToPost(entity);

        log.info("Saved post with id {} to cache: {}", entity.getId(), entity);

        return CompletableFuture.completedFuture(entity);
    }

    private CommentRedisCache updateOrSave(CommentRedisCache entity) {

        commentRedisRepository.findById(entity.getId()).ifPresent(commentRedisRepository::delete);
        return commentRedisRepository.save(entity);
    }
}
