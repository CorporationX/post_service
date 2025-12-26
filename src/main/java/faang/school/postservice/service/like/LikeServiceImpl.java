package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.redis.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;
    private final PostService postService;
    private final LikeEventPublisher likeEventPublisher;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;

    @Transactional
    public LikeDto likePost(long postId, long userId) {
        log.info("like post requested: postId={}, userId={}", postId, userId);
        ensureUserExists(userId);

        likeRepository.findByPostIdAndUserId(postId, userId).ifPresent(l -> {
            log.warn("like post already exists: postId={}, userId={}", postId, userId);
            throw new IllegalStateException("Like already exists for this post and user");
        });

        Post post = postMapper.toPost(postService.getPostById(postId));
        Like like = Like.builder().userId(userId).post(post).comment(null).build();
        Like saved = likeRepository.save(like);
        log.info("like post success: postId={}, userId={}", postId, userId);

        LikeEvent event = new LikeEvent(
                postId,
                post.getAuthorId(),
                userId,
                LocalDateTime.now()
        );

        likeEventPublisher.publish(event);

        return likeMapper.toDto(saved);
    }

    @Transactional
    public void unlikePost(long postId, long userId) {
        log.info("unlike post requested: postId={}, userId={}", postId, userId);
        ensureUserExists(userId);

        likeRepository.findByPostIdAndUserId(postId, userId).
                ifPresentOrElse(
                        l -> likeRepository.deleteByPostIdAndUserId(postId, userId),
                        () -> log.debug("No like to remove for postId={} userId={}", postId, userId));
    }

    @Transactional
    public LikeDto likeComment(long commentId, long userId) {
        log.info("like comment requested: commentId={}, userId={}", commentId, userId);
        ensureUserExists(userId);

        likeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresent(
                        l -> {
                            log.warn("like comment already exists: commentId={}, userId={}", commentId, userId);
                            throw new IllegalStateException("Like already exists for this comment and user");
                        }
                );

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment with provided Id does not exist!"));
        Like like = Like.builder()
                .userId(userId)
                .post(null)
                .comment(comment)
                .build();
        Like saved = likeRepository.save(like);
        log.info("like comment success: commentId={}, userId={}", commentId, userId);

        return likeMapper.toDto(saved);
    }

    @Transactional
    public void unlikeComment(long commentId, long userId) {
        log.info("unlike comment requested: commentId={}, userId={}", commentId, userId);
        ensureUserExists(userId);

        likeRepository.findByCommentIdAndUserId(commentId, userId).
                ifPresentOrElse(
                        l -> likeRepository.deleteByCommentIdAndUserId(commentId, userId),
                        () -> log.debug("No like to remove for commentId={} userId={}", commentId, userId));
    }

    private void ensureUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            log.warn("user not found: userId={}", userId);
            throw new IllegalArgumentException("User not found: " + userId, e);
        }
    }
}
