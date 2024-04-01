package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.RedisComment;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.PriorityQueue;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class FeedService {


    private final RedisPostRepository redisPostRepository;
    private final CommentService commentService;
    private final RedisCommentMapper redisCommentMapper;
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final RedisPostMapper redisPostMapper;
    @Value("${newsfeed.comments_queue_size}")
    private int commentQueueSize;
    private PriorityQueue<RedisComment> comments = new PriorityQueue<>(commentQueueSize);

    public void addComment(long postId, long commentId) {
        CommentDto comment =  commentService.getComment(commentId);
        RedisComment redisComment = redisCommentMapper.toRedisComment(comment);
        RedisPost redisPost = redisPostRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post with id: " + commentId + " does not exist"));
        comments = redisPost.getComments();
        if (comments.size() == 3) {
            comments.remove();
            comments.add(redisComment);
        }
        redisPost.setComments(comments);
        redisPostRepository.save(redisPost);
    }
}
