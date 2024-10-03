package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.post.UnexistentPostException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.post.PostCacheValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostCacheRepository {

    private static final String CACHE_PREFIX = "post:";
    private static final String COMMENT_SUFFIX = ":comments";
    private static final String LIKE_SUFFIX = ":likes";
    private static final String VIEW_SUFFIX = ":views";

    private final RedisTemplate<String, Object> redisTemplate;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final ObjectMapper objectMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    private final PostCacheValidator postCacheValidator;

    @Value("${cache.post-ttl-seconds}")
    private long timeToLive;

    @Value("${cache.comments.max}")
    private int maxComments;

    public void save(long postId, PostDto post) {
        String key = CACHE_PREFIX + postId;
        redisTemplate.opsForValue()
                .set(key, post, timeToLive, TimeUnit.SECONDS);
    }

    public PostDto getPost(long postId) {
        String key = CACHE_PREFIX + postId;
        Object value = redisTemplate.opsForValue().get(key);

        if(value != null) {
            return objectMapper.convertValue(value, PostDto.class);
        }

        return findPost(postId);
    }

    public void addComment(long postId, CommentDto comment) {
        String key = CACHE_PREFIX + postId + COMMENT_SUFFIX;

        try {
            String serializedComment = objectMapper.writeValueAsString(comment);

            redisTemplate.opsForList().leftPush(key, serializedComment);
            redisTemplate.opsForList().trim(key, 0, maxComments - 1);
            redisTemplate.expire(key, timeToLive, TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new RuntimeException("Comment deserialization error!", e);
        }
    }

    public List<CommentDto> getComments(long postId) {
        String key = CACHE_PREFIX + postId + COMMENT_SUFFIX;
        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);

        if(!postCacheValidator.validateComments(range, maxComments)) {
            return deserializeComments(range);
        }

        return findComments(postId);
    }

    public void incrementLike(long postId) {
        String key = CACHE_PREFIX + postId + LIKE_SUFFIX;
        redisTemplate.opsForValue().increment(key);
    }

    public void setLike(long postId, long likes) {
        String key = CACHE_PREFIX + postId + LIKE_SUFFIX;
        redisTemplate.opsForValue()
                .set(key, likes, timeToLive, TimeUnit.SECONDS);
    }

    public Long getLikes(long postId) {
        String key = CACHE_PREFIX + postId + LIKE_SUFFIX;
        Object value = redisTemplate.opsForValue().get(key);

        if(value != null) {
            return objectMapper.convertValue(value, Long.class);
        }

        return findLikes(postId);
    }

    public void incrementView(long postId) {
        String key = CACHE_PREFIX + postId + VIEW_SUFFIX;
        redisTemplate.opsForValue().increment(key);
    }

    public void setViews(long postId, long views) {
        String key = CACHE_PREFIX + postId + VIEW_SUFFIX;
        redisTemplate.opsForValue()
                .set(key, views, timeToLive, TimeUnit.SECONDS);
    }

    public Long getViews(long postId) {
        String key = CACHE_PREFIX + postId + VIEW_SUFFIX;
        Object value = redisTemplate.opsForValue().get(key);

        if(value != null) {
            return objectMapper.convertValue(value, Long.class);
        }

        return findViews(postId);
    }

    private List<CommentDto> deserializeComments(List<Object> serializedComments) {
        return serializedComments.stream()
                .map(obj -> {
                    try {
                        return objectMapper.readValue((String) obj, CommentDto.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Comment deserialization error!", e);
                    }
                })
                .collect(Collectors.toList());
    }

    private PostDto findPost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new UnexistentPostException(postId));

        PostDto postDto = postMapper.toDto(post);
        save(postDto.getId(), postDto);
        return postDto;
    }

    private List<CommentDto> findComments(long postId) {
        Pageable commentPageable = PageRequest.of(0, maxComments);
        List<Comment> comments = commentRepository
                .findByPostIdWithLikesOrderByCreatedAtDesc(postId, commentPageable);

        return comments.stream()
                .map(comment -> {
                    CommentDto commentDto = commentMapper.toDto(comment);

                    addComment(postId, commentDto);

                    return commentDto;
                })
                .collect(Collectors.toList());
    }

    private long findLikes(long postId) {
        long likes = getPost(postId).getLikesCount();
        setLike(postId, likes);
        return likes;
    }

    private long findViews(long postId) {
        long views = getPost(postId).getViews();
        setViews(postId, views);
        return views;
    }
}
