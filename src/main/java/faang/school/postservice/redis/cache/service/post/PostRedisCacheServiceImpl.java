package faang.school.postservice.redis.cache.service.post;

import faang.school.postservice.redis.cache.entity.PostRedisCache;
import faang.school.postservice.redis.cache.repository.PostRedisRepository;
import faang.school.postservice.redis.cache.service.RedisOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@Async("cacheTaskExecutor")
public class PostRedisCacheServiceImpl implements PostRedisCacheService {

    private final PostRedisRepository postRedisRepository;
    private final RedisOperations redisOperations;

    @Override
    public CompletableFuture<PostRedisCache> save(PostRedisCache entity) {

        entity = redisOperations.updateOrSave(postRedisRepository, entity, entity.getId());

        log.info("Saved post with id {} to cache: {}", entity.getId(), entity);

        return CompletableFuture.completedFuture(entity);
    }

    @Override
    public void incrementLikes(long postId) {

        redisOperations.findById(postRedisRepository, postId).ifPresent(post -> {
            post.setLikesCount(post.getLikesCount() + 1);
            redisOperations.updateOrSave(postRedisRepository, post, postId);
        });
    }

    @Override
    public void incrementViews(long postId) {

        redisOperations.findById(postRedisRepository, postId).ifPresent(post -> {
            post.setViewsCount(post.getViewsCount() + 1);
            redisOperations.updateOrSave(postRedisRepository, post, postId);
        });
    }

    @Override
    public void decrementLikes(long postId) {

        redisOperations.findById(postRedisRepository, postId).ifPresent(post -> {
            post.setLikesCount(post.getLikesCount() - 1);
            redisOperations.updateOrSave(postRedisRepository, post, postId);
        });
    }

    @Override
    public void deleteById(long postId) {

        redisOperations.deleteById(postRedisRepository, postId);
        log.info("Deleted post with id={} from cache", postId);
    }
}
