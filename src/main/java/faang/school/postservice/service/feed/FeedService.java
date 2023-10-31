package faang.school.postservice.service.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisComment;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final RedisPostRepository redisPostRepository;
    private final CommentService commentService;
    private final RedisCommentMapper redisCommentMapper;
    private final ZSetOperations<Long, Object> feeds;
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final RedisPostMapper redisPostMapper;

    public List<RedisPost> getFeed(Optional<Long> optionalPostId) {
        Stream<Long> postIdStream;
        if (optionalPostId.isEmpty()) {
            postIdStream = getPostId(0, 20);
        } else {
            postIdStream = getPostId(optionalPostId.get(), 20);
        }
        return getPostsById(postIdStream);
    }

    public Stream<Long> getPostId(long start, long limit) {
        long userId = userContext.getUserId();
        return feeds.range(userId, start, start + limit - 1).stream()
                .map(postId -> (Long) postId).filter(Objects::nonNull);
    }

    public List<RedisPost> getPostsById(Stream<Long> postIdStream) {
        return postIdStream.map(postId -> {
                    var optionalRedisPost = redisPostRepository.findById(postId);
                    if (optionalRedisPost.isEmpty()) {
                        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException());
                        return redisPostMapper.toRedisEntity(post);
                    } else {
                        return optionalRedisPost.get();
                    }
                })
                .toList();
    }

    public void addPostToFeeds(long postId, List<Long> followersId, LocalDateTime publishedAt) {
        followersId.forEach(userId -> addPost(userId, postId, publishedAt));
    }

    public void addPost(long userId, long postId, LocalDateTime publishedAt) {
        Double score = timeToScore(publishedAt);
        feeds.add(userId, postId, score);
        if (feeds.size(userId) > 500) {
            feeds.remove(userId, 500, feeds.size(userId) - 1);
        }
    }

    public void addCommentToPost(long postId, long commentId) {
        Comment comment = commentService.getComment(commentId);
        RedisComment redisComment = redisCommentMapper.toRedisEntity(comment);
        RedisPost redisPost = redisPostRepository.findById(postId).orElseThrow();
        Queue<RedisComment> redisComments = redisPost.getComments();
        redisComments.add(redisComment);
        if (redisComments.size() > 3) {
            redisComments.remove();
        }
        redisPostRepository.save(redisPost);
    }

    public void addLikeToPost(long postId) {
        RedisPost redisPost = redisPostRepository.findById(postId).orElseThrow();
        redisPost.setLikes(redisPost.getLikes() + 1);
    }

    public void addViewToPost(long postId) {
        RedisPost redisPost = redisPostRepository.findById(postId).orElseThrow();
        redisPost.setViews(redisPost.getViews() + 1);
    }

    private Double timeToScore(LocalDateTime time) {
        long secondsSinceEpoch = time.toEpochSecond(ZoneOffset.UTC);
        long nanos = time.getNano();
        return secondsSinceEpoch + (nanos / 1e9);
    }
}
