package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final ExecutorService redisCacheExecutor;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ExecutorService kafkaExecutor;
    private final KafkaTemplate<String, LikeEvent> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;



    @Override
    @Transactional
    public void addLikePost(Long postId, Long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new IllegalArgumentException("Лайк под этим постом уже оставлен пользователем с id: " + userId + " id поста: " + postId);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Пост с id " + postId + " не найден"));
        Like like = new Like();
        like.setPost(post);
        like.setUserId(userId);
        likeRepository.save(like);

        kafkaExecutor.execute(() -> {
            LikeEvent event = new LikeEvent("POST_LIKE", userId, postId, null, LocalDateTime.now());
            kafkaTemplate.send("likes", event);
        });

        redisCacheExecutor.execute(() -> {
            String key = "post_likes:" + postId;
            redisTemplate.opsForValue().increment(key);
        });
    }

    @Override
    @Transactional
    public void removeLikePost(Long postId, Long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isEmpty()) {
            throw new IllegalArgumentException("Лайк под этим постом не найден у пользователя с id: " + userId + " id поста: " + postId);
        }

        likeRepository.deleteByPostIdAndUserId(postId, userId);

        kafkaExecutor.execute(() -> {
            LikeEvent event = new LikeEvent("POST_UNLIKE", userId, postId, null, LocalDateTime.now());
            kafkaTemplate.send("likes", event);
        });

        redisCacheExecutor.execute(() -> {
            String key = "post_likes:" + postId;
            redisTemplate.opsForValue().decrement(key);
        });
    }

    @Override
    @Transactional
    public void addLikeComment(Long commentId, Long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new IllegalArgumentException("Лайк под этим комментарием уже оставлен пользователем с id: " + userId + " id комментария: " + commentId);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Комментарий с id " + commentId + " не найден"));

        Like like = Like.builder()
                .comment(comment)
                .userId(userId)
                .build();
        Like likeResult = likeRepository.save(like);

        kafkaExecutor.execute(() -> {
            LikeEvent event = new LikeEvent("COMMENT_LIKE", userId, null, commentId, LocalDateTime.now());
            kafkaTemplate.send("likes", event);
        });

        redisCacheExecutor.execute(() -> {
            String key = "comment_likes:" + commentId;
            redisTemplate.opsForValue().increment(key);
        });
    }

    @Override
    @Transactional
    public void removeLikeComment(Long commentId, Long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isEmpty()) {
            throw new IllegalArgumentException("Лайк под этим комментарием не найден у пользователя с id: " + userId + " id комментария: " + commentId);
        }

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);

        kafkaExecutor.execute(() -> {
            LikeEvent event = new LikeEvent("COMMENT_UNLIKE", userId, null, commentId, LocalDateTime.now());
            kafkaTemplate.send("likes", event);
        });

        redisCacheExecutor.execute(() -> {
            String key = "comment_likes:" + commentId;
            redisTemplate.opsForValue().decrement(key);
        });
    }

    @Cacheable(value = "postLikes", key = "#postId")
    public int getPostLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Cacheable(value = "commentLikes", key = "#commentId")
    public int getCommentLikeCount(Long commentId) {
        return likeRepository.countByCommentId(commentId);
    }
}
