package faang.school.postservice.redis.service;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostRequirementsException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.PostCache;
import faang.school.postservice.redis.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheRepository postCacheRepository;
    private final PostCacheMapper postCacheMapper;
    private final RedisProperties redisProperties;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public void addPostView(Long postId) {
        if (postCacheRepository.existsById(postId)) {
            incrementConcurrentPostViews(postId);
        } else {
            createAndCachePost(postId);
        }
    }

    public void addCommentToCachedPost(Long postId, CommentDto commentDto) {
        PostCache postCache = postCacheRepository.findById(postId)
                .orElseGet(() -> createAndCachePost(postId));

        if (postCache != null) {
            addComment(postCache, commentDto);
        }
    }

    private void incrementConcurrentPostViews(Long postId) {
        String postCacheViews = redisProperties.getPostCacheViews();
        redisTemplate.opsForHash()
                .increment(generateCachePostKey(postId), postCacheViews, 1);
    }

    private PostCache createAndCachePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostRequirementsException("Post not found"));
        return savePostCache(postMapper.toDto(post));
    }

    public void addComment(PostCache postCache, CommentDto commentDto) {
        List<CommentDto> comments = postCache.getComments();

        if (comments == null) {
            comments = new ArrayList<>();
            postCache.setComments(comments);
        }
        ensureCommentCapacity(comments);
        comments.add(0, commentDto);
    }

    private void ensureCommentCapacity(List<CommentDto> comments) {
        Integer maxCommentsQuantity = redisProperties.getPostCacheMaxCommentsQuantity();
        if (comments.size() == maxCommentsQuantity) {
            comments.remove(comments.size() - 1);
        }
    }

    private String generateCachePostKey(Long postId) {
        String postCacheKeyPrefix = redisProperties.getFeedCacheKeyPrefix();
        return postCacheKeyPrefix + postId;
    }

    public List<PostCache> getPostCacheByIds(List<Long> postIds) {
        var iterable = postCacheRepository.findAllById(postIds);
        return StreamSupport.stream(iterable.spliterator(), false)
                .toList();
    }

    public PostCache savePostCache(PostDto postDto) {
        PostCache postCache = postCacheMapper.toPostCache(postDto);
        return postCacheRepository.save(postCache);
    }
}