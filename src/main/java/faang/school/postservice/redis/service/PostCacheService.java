package faang.school.postservice.redis.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.PostCache;
import faang.school.postservice.redis.repository.PostCacheRepository;
import faang.school.postservice.service.post.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class PostCacheService {
    @Value("{spring.data.redis.post-cache.views}")
    private String postCacheViewsField;
    @Value("{spring.data.redis.post-cache.likes}")
    private String postCacheLikesField;
    @Value("{spring.data.redis.post-cache.comments-in-post:3}")
    private int commentsInPostQuantity;
    @Value("{spring.data.redis.post-cache.key-prefix}")
    private String postCacheKeyPrefix;
    @Value("spring.data.redis.post-cache.comments-in-post:3")
    private int maxCommentsQuantity;

    @Qualifier("redisCacheTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheRepository postCacheRepository;
    private final PostCacheMapper postCacheMapper;
    private final PostService postService;

    public PostCacheService(RedisTemplate<String, Object> redisTemplate, PostCacheRepository postCacheRepository,
                            PostCacheMapper postCacheMapper, PostService postService) {
        this.redisTemplate = redisTemplate;
        this.postCacheRepository = postCacheRepository;
        this.postCacheMapper = postCacheMapper;
        this.postService = postService;
    }

    public void addPostView(Long postId){
        if (postCacheRepository.existsById(postId)) {
            incrementConcurrentPostViews(postId);
        }else {
            var postDto = postService.getPost(postId);
            savePostCache(postDto);
        }
    }

    public void incrementConcurrentPostLikes(Long postId) {
        if (postCacheRepository.existsById(postId)){
            redisTemplate.opsForHash()
                    .increment(generateCachePostKey(postId), postCacheLikesField, 1);
        } else {
            var postDto = postService.getPost(postId);
            savePostCache(postDto);
        }
    }

    public void addCommentToCachedPost(Long postId, CommentDto commentDto) {
        var postCache = postCacheRepository.findById(postId)
                .orElseGet(() -> createAndCachePost(postId));

        if (postCache != null) {
            addComment(postCache, commentDto);
        }
    }

    private void incrementConcurrentPostViews(Long postId) {
        redisTemplate.opsForHash()
                .increment(generateCachePostKey(postId), postCacheViewsField, 1);
    }

    private PostCache createAndCachePost(Long postId) {
        var postDto = postService.getPost(postId);
        return savePostCache(postDto);
    }

    public void addComment(PostCache postCache, CommentDto commentDto) {
        var comments = postCache.getComments();
        ensureCapacity(comments);
        comments.add(0, commentDto);
    }

    private void ensureCapacity(List<CommentDto> comments) {
        if (comments.size() == maxCommentsQuantity) {
            comments.remove(comments.size() - 1);
        }
    }

    private String generateCachePostKey(Long postId) {
        return postCacheKeyPrefix + postId;
    }

    public List<PostCache> getPostCacheByIds(List<Long> postIds) {
        var iterable = postCacheRepository.findAllById(postIds);
        return StreamSupport.stream(iterable.spliterator(), false)
                .toList();
    }

    public PostCache savePostCache(PostDto postDto) {
        var postCache = postCacheMapper.toPostCache(postDto);
        return postCacheRepository.save(postCache);
    }
}