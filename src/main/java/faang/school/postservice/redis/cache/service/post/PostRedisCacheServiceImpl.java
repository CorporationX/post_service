package faang.school.postservice.redis.cache.service.post;

import faang.school.postservice.redis.cache.entity.PostRedisCache;
import faang.school.postservice.redis.cache.repository.PostRedisRepository;
import faang.school.postservice.redis.cache.service.RedisOperations;
import faang.school.postservice.redis.cache.service.author.AuthorRedisCacheService;
import faang.school.postservice.redis.cache.service.feed.FeedRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@Async("postsCacheTaskExecutor")
public class PostRedisCacheServiceImpl implements PostRedisCacheService {

    private final PostRedisRepository postRedisRepository;
    private final RedisOperations redisOperations;
    private final FeedRedisService feedRedisService;
    private final AuthorRedisCacheService authorRedisCacheService;

    @Override
    public CompletableFuture<PostRedisCache> save(PostRedisCache entity, List<Long> subscriberIds) {

        entity = redisOperations.updateOrSave(postRedisRepository, entity, entity.getId());

        PostRedisCache finalEntity = entity;
        authorRedisCacheService.save(entity.getAuthor());
        subscriberIds.forEach(subscriberId -> feedRedisService.addPostToFeed(finalEntity, subscriberId));

        log.info("Saved post with id {} to cache: {}", entity.getId(), entity);

        return CompletableFuture.completedFuture(entity);
    }

    @Override
    public void deleteById(long postId, List<Long> subscriberIds) {

        PostRedisCache post = redisOperations.findById(postRedisRepository, postId).orElse(null);
        redisOperations.deleteById(postRedisRepository, postId);

        subscriberIds.forEach(subscriberId -> feedRedisService.deletePostFromFeed(post, subscriberId));

        log.info("Deleted post with id={} from cache", postId);
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
}
