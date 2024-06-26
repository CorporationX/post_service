package faang.school.postservice.service.redis;

import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCachePostService {

    private final RedisPostRepository redisPostRepository;
    private final RedisKeyValueTemplate redisKeyValueTemplate;
    private final PostMapper postMapper;

    @Value("${spring.data.redis.ttl}")
    private Long ttl;

    public void sendPostInCacheRedis(Post post) {
        PostRedis postRedis = buildPostRedis(post);
        log.info("Send post in redis: {}", post);

        try {
            savePostRedisWithOptimisticLock(postRedis, post.getId());
        } catch (Exception e) {
            handleSaveException(post, postRedis, e);
        }
    }

    private PostRedis buildPostRedis(Post post) {
        return PostRedis.builder()
                .id(String.valueOf(post.getId()))
                .postDto(postMapper.toDto(post))
                .expiration(ttl)
                .version(0L)
                .build();
    }

    private void savePostRedisWithOptimisticLock(PostRedis postRedis, Long postId) {
        redisPostRepository.findById(postId).ifPresentOrElse(
                existingPostRedis -> {
                    if (existingPostRedis.getVersion().equals(postRedis.getVersion())) {
                        postRedis.incrementVersion();
                        redisPostRepository.save(postRedis);
                    } else {
                        log.error("Version mismatch for post: {}", postId);
                        throw new OptimisticLockingFailureException("Version mismatch");
                    }
                },
                () -> redisPostRepository.save(postRedis)
        );

        if (!redisPostRepository.existsById(postId)) {
            log.error("Failed to save post to Redis cache: {}", postId);
        }
    }

    private void handleSaveException(Post post, PostRedis postRedis, Exception e) {
        log.error("Error saving post to Redis: ", e);
        redisPostRepository.findById(post.getId()).ifPresentOrElse(
                existingPostRedis -> {
                    if (existingPostRedis.getVersion().equals(postRedis.getVersion())) {
                        postRedis.incrementVersion();
                        redisKeyValueTemplate.update(postRedis);
                    } else {
                        log.error("Version mismatch during error handling for post: {}", post.getId());
                        throw new OptimisticLockingFailureException("Version mismatch");
                    }
                },
                () -> redisPostRepository.save(postRedis)
        );
    }
    public PostRedis getPostFromRedis(long id){
        return redisPostRepository.findById(id).orElseThrow();
    }

}
