package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.feed.PostCacheMapper;
import faang.school.postservice.model.feed.PostCache;
import faang.school.postservice.repository.feed.PostCacheRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostCacheService {

    @Value("${spring.data.redis.post-cache.views}")
    private String postCacheViewsField;

    @Value("${spring.data.redis.post-cache.likes}")
    private String postCacheLikesField;

    @Value("${spring.data.redis.post-cache.comments-in-post}")
    private int maxCommentsQuantity;

    @Value("${spring.data.redis.post-cache.key-prefix}")
    private String postCacheKeyPrefix;

    private final PostCacheRepository postCacheRepository;
    private final PostCacheMapper postCacheMapper;
    private final PostService postService;

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(@Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addPostView(Long postId) {
        if (postCacheRepository.existsById(postId)) {
            incrementConcurrentPostViews(postId);
        } else {
            PostDto postDto = postService.getPost(postId);
            savePostCache(postDto);
        }
    }

    public void incrementConcurrentPostLikes(Long postId) {
        if (postCacheRepository.existsById(postId)) {
            redisTemplate.opsForHash().increment(generateCachePostKey(postId), postCacheLikesField, 1);
        } else {
            PostDto postDto = postService.getPost(postId);
            savePostCache(postDto);
        }
    }

    public void addCommentToCachedPost(Long postId, CommentDto commentDto) {
        Optional<PostCache> optional = postCacheRepository.findById(postId);
        PostCache postCache = optional.orElseGet(() -> createAndCachePost(postId));

        if (postCache != null) {
            addComment(postCache, commentDto);
        }
    }

    public void addComment(PostCache postCache, CommentDto commentDto) {
        List<CommentDto> comments = new ArrayList<>(postCache.getComments());
        ensureCapacity(comments);
        comments.add(0, commentDto);
        postCache.setComments(new CopyOnWriteArraySet<>(comments));
    }

    public List<PostCache> getPostCacheByIds(List<Long> postIds) {
        Iterable<PostCache> iterable = postCacheRepository.findAllById(postIds);
        return StreamSupport.stream(iterable.spliterator(), false).toList();
    }

    public PostCache savePostCache(PostDto postDto) {
        PostCache postCache = postCacheMapper.toPostCache(postDto);
        return postCacheRepository.save(postCache);
    }

    private void incrementConcurrentPostViews(Long postId) {
        redisTemplate.opsForHash().increment(generateCachePostKey(postId), postCacheViewsField, 1);
    }

    private PostCache createAndCachePost(Long postId) {
        PostDto postDto = postService.getPost(postId);
        return savePostCache(postDto);
    }

    private void ensureCapacity(List<CommentDto> comments) {
        if (comments.size() == maxCommentsQuantity) {
            comments.remove(comments.size() - 1);
        }
    }

    private String generateCachePostKey(Long postId) {
        return postCacheKeyPrefix + postId;
    }
}