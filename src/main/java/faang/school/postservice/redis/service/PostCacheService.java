package faang.school.postservice.redis.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.model.CommentEvent;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.PostCache;
import faang.school.postservice.redis.repository.PostCacheRepository;
import faang.school.postservice.service.post.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostCacheService {

    @Value("{spring.data.redis.post-cache.key-prefix}")
    private String postCacheKeyPrefix;
    @Value("{spring.data.redis.post-cache.likes}")
    private String cacheLikesField;
    @Value("{spring.data.redis.post-cache.views}")
    private String cacheViewField;

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheRepository postCacheRepository;
    private final PostService postService;
    private final PostCacheMapper postCacheMapper;

    public PostCacheService(RedisTemplate<String, Object> redisTemplate, PostCacheRepository postCacheRepository, PostService postService, PostCacheMapper postCacheMapper) {
        this.redisTemplate = redisTemplate;
        this.postCacheRepository = postCacheRepository;
        this.postService = postService;
        this.postCacheMapper = postCacheMapper;
    }

    @Async
    public void savePostEvent(PostDto postDto) {
        PostCache postCache = postCacheMapper.toPostCache(postDto);
        postCacheRepository.save(postCache);
    }

    public void incrementLikes(Long postId) {
        if (!postCacheRepository.existsById(postId)) {
            savePostCache(postId);
        }
        redisTemplate.opsForHash()
                .increment(generateCachePostKey(postId), cacheLikesField, 1);

    }

    public void incrementView(Long postId) {
        if (!postCacheRepository.existsById(postId)) {
            savePostCache(postId);
        }
        redisTemplate.opsForHash()
                .increment(generateCachePostKey(postId), cacheViewField, 1);
    }

    // TODO: не потока безопасно, подумать !!
    public void addComments(CommentEvent commentEvent) {
        PostCache postCache = postCacheRepository.findById(commentEvent.postId())
                .orElseGet(() -> savePostCache(commentEvent.postId()));
        if (postCache == null) {
            throw new NullPointerException("postCache is null");
        }
        postCache.getComments().add(commentEvent);
    }

    private String generateCachePostKey(Long postId) {
        return postCacheKeyPrefix + postId;
    }

    public PostCache savePostCache(Long postId) {
        var postDto = postService.getPost(postId);
        var postCache = postCacheMapper.toPostCache(postDto);
        postCacheRepository.save(postCache);
        return postCache;
    }

}
