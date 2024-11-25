package faang.school.postservice.aspect;

import faang.school.postservice.annotation.post.CachePost;
import faang.school.postservice.config.redis.CacheConfigProperties;
import faang.school.postservice.mapper.redis.PostRedisMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CacheOperation;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.redis.RedisPostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CachingAspect {

    private final RedisPostsRepository redisPostsRepository;
    private final PostRedisMapper postMapper;
    private final CacheConfigProperties cacheConfigProperties;
    @Around("@annotation(cachePost)")
    public Object cachePostMethod(ProceedingJoinPoint joinPoint, CachePost cachePost) throws Throwable {
        Object[] args = joinPoint.getArgs();

        if (args.length == 0 || !(args[0] instanceof Long)) {
            log.warn("Method annotated with @CachePost expects at least one Long argument as Post ID.");
            return joinPoint.proceed();
        }

        Long postId = (Long) args[0];
        CacheOperation operation = cachePost.operation();
        String cacheKey = cachePost.keyPrefix() + postId;

        if (operation == CacheOperation.DELETE) {
            log.info("Handling DELETE operation for Post ID: {}", postId);
            Object result = joinPoint.proceed();

            try {
                redisPostsRepository.deleteById(postId);
                log.info("Deleted cache entry for Post ID: {}", postId);
            } catch (Exception e) {
                log.error("Failed to delete cache for Post ID {}: {}", postId, e.getMessage());
            }

            return result;
        }

        try {
            Optional<PostRedis> cachedPostOpt = redisPostsRepository.findById(postId);
            if (cachedPostOpt.isPresent()) {
                log.info("Cache hit for key {}", cacheKey);
                Post postFromCache = postMapper.postRedisToPost(cachedPostOpt.get());
                return postFromCache;
            }

            log.info("Cache miss for key {}. Proceeding with method execution.", cacheKey);
            Object result = joinPoint.proceed();

            if (result != null && result instanceof Post) {
                Post postResult = (Post) result;
                long ttlToSet = determineTtl(operation);
                PostRedis postRedis = postMapper.postToPostRedis(postResult);

                if (ttlToSet > 0) {
                    redisPostsRepository.saveWithTTL(postRedis, ttlToSet);
                    log.info("Cached Post ID {} with TTL {} seconds.", postId, ttlToSet);
                } else {
                    redisPostsRepository.save(postRedis);
                    log.info("Cached Post ID {} without TTL.", postId);
                }
            }

            return result;
        } catch (Exception e) {
            log.error("Caching operation failed for Post ID {}: {}", postId, e.getMessage());
            throw e;
        }
    }

    private long determineTtl(CacheOperation operation) {
        switch (operation) {
            case PUBLISH:
                return cacheConfigProperties.getPublishTtlSeconds();
            case UPDATE:
                return cacheConfigProperties.getUpdateTtlSeconds();
            case DELETE:
                return cacheConfigProperties.getDeleteTtlSeconds();
            case DEFAULT:
            default:
                return cacheConfigProperties.getDefaultTtlSeconds();
        }
    }
}
