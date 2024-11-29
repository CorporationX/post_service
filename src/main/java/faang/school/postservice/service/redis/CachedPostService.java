package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.CommentNewsFeedDto;
import faang.school.postservice.mapper.redis.CachedPostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CachedPost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.CachedPostRepository;
import faang.school.postservice.service.post.PostQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class CachedPostService {
    @Value("${spring.data.redis.post-cache.views}")
    private String postCacheViewsField;

    @Value("${spring.data.redis.post-cache.likes}")
    private String postCacheLikesField;

    @Value("${spring.data.redis.post-cache.post}")
    private String postCacheKeyPrefix;

    @Value("${spring.data.redis.post-cache.comments-in-post:3}")
    private int maxCommentsQuantity;

    @Qualifier("redisCacheTemplate")
    private final RedisTemplate<String, CachedPost> redisTemplate;
    private final CachedPostRepository cachedPostRepository;
    private final CachedPostMapper cachedPostMapper;
    private final PostQueryService postQueryService;
    private final PostRepository postRepository;
    public CachedPostService(RedisTemplate<String, CachedPost> redisTemplate, CachedPostRepository cachedPostRepository, CachedPostMapper cachedPostMapper, PostQueryService postQueryService, PostRepository postRepository) {
        this.redisTemplate = redisTemplate;
        this.cachedPostRepository = cachedPostRepository;
        this.cachedPostMapper = cachedPostMapper;
        this.postQueryService = postQueryService;
        this.postRepository = postRepository;
    }
    public void addPostView(Long postId, Long viewCount) {
        String cacheKey = generateCachePostKey(postId);

        try {
            redisTemplate.opsForHash().increment(cacheKey, postCacheViewsField, viewCount);
            log.info("Post ID {}: Incremented views in cache by {}", postId, viewCount);
        } catch (Exception e) {
            log.error("Failed to increment views for post ID {}. Falling back to DB.", postId, e);

            Post post = postQueryService.findPostById(postId);
            CachedPost cachedPost = savePostCache(post);

            Long updatedViews = cachedPost.getViews() + viewCount;
            cachedPost.setViews(updatedViews);
            cachedPostRepository.save(cachedPost);
        }
    }

    public void incrementPostLikes(Long postId) {
        String cacheKey = generateCachePostKey(postId);

        try {
            redisTemplate.opsForHash().increment(cacheKey, postCacheLikesField, 1);
            log.info("Post ID {}: Incremented likes in cache", postId);
        } catch (Exception e) {
            log.error("Failed to increment likes for post ID {}. Falling back to DB.", postId, e);

            Post post = postQueryService.findPostById(postId);
            CachedPost cachedPost = savePostCache(post);

            cachedPost.setLikes((long) post.getLikes().size() + 1);
            cachedPostRepository.save(cachedPost);
        }
    }

    public void decrementPostLikes(Long postId) {
        String cacheKey = generateCachePostKey(postId);

        try {
            redisTemplate.opsForHash().increment(cacheKey, postCacheLikesField, -1);
            log.info("Post ID {}: Decremented likes in cache", postId);
        } catch (Exception e) {
            log.error("Failed to decrement likes for post ID {}. Falling back to DB.", postId, e);

            Post post = postQueryService.findPostById(postId);
            CachedPost cachedPost = savePostCache(post);

            cachedPost.setLikes((long) post.getLikes().size() - 1);
            cachedPostRepository.save(cachedPost);
        }
    }

    public void addCommentToCachedPost(Long postId, CommentNewsFeedDto commentDto) {
        CachedPost cachedPost = cachedPostRepository.findById(postId)
                .orElseGet(() -> {
                    Post post = postQueryService.findPostById(postId);
                    return savePostCache(post);
                });

        if (cachedPost != null) {
            ConcurrentLinkedQueue<CommentNewsFeedDto> comments = cachedPost.getComments();
            ensureCapacity(comments);
            comments.add(commentDto);
            cachedPostRepository.save(cachedPost);
            log.info("Post ID {}: Added comment to CachedPost", postId);
        }
    }

    private void ensureCapacity(ConcurrentLinkedQueue<CommentNewsFeedDto> comments) {
        if (comments.size() >= maxCommentsQuantity) {
            comments.poll();
        }
    }

    public List<CachedPost> getCachedPostByIds(List<Long> postIds) {
        Iterable<CachedPost> iterable = cachedPostRepository.findAllById(postIds);
        return StreamSupport.stream(iterable.spliterator(), false).toList();
    }

    public CachedPost savePostCache(Post post) {
        CachedPost cachedPost = cachedPostMapper.toCachedPost(post);
        redisTemplate.opsForValue().set(generateCachePostKey(post.getId()), cachedPost);
        return cachedPost;
    }
    @Scheduled(fixedRateString = "${spring.data.redis.post-cache.sync-rate-ms:60000}")
    public void syncViewsFromCacheToDB() {
        Set<String> cacheKeys = redisTemplate.keys(postCacheKeyPrefix + "*");

        if (cacheKeys == null || cacheKeys.isEmpty()) {
            log.info("No cached views to synchronize with the database.");
            return;
        }

        for (String cacheKey : cacheKeys) {
            try {
                Long postId = extractPostIdFromCacheKey(cacheKey);
                Long cachedViews = (Long) redisTemplate.opsForHash().get(cacheKey, postCacheViewsField);

                if (cachedViews != null && cachedViews > 0) {
                    Post post = postQueryService.findPostById(postId);
                    Long newDbViews = post.getViews() + cachedViews.intValue();

                    post.setViews(newDbViews);
                    postRepository.save(post);

                    log.info("Post ID {}: Synchronized {} views from cache to DB.", postId, cachedViews);
                }
            } catch (Exception e) {
                log.error("Failed to synchronize views for cacheKey: {}", cacheKey, e);
            }
        }
    }

    private Long extractPostIdFromCacheKey(String cacheKey) {
        return Long.parseLong(cacheKey.replace(postCacheKeyPrefix, ""));
    }

    private String generateCachePostKey(Long postId) {
        return postCacheKeyPrefix + postId;
    }
}