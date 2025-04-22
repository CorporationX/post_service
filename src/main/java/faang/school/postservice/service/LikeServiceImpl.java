package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentLikeDto;
import faang.school.postservice.dto.PostLikeDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;

    @Transactional
    @Override
    public PostLikeDto likePost(Long userId, Long postId) {
        validateUser(userId);
        checkLikeDoesNotExist(postId, userId, true);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with ID " + postId + " not found"));

        Like like = Like.builder()
                .userId(userId)
                .post(post)
                .build();

        like = likeRepository.save(like);
        log.info("User {} liked post {}", userId, postId);

        return likeMapper.toPostLikeDto(like);
    }

    @Transactional
    @Override
    public CommentLikeDto likeComment(Long userId, Long commentId) {
        validateUser(userId);
        checkLikeDoesNotExist(commentId, userId, false);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with ID " + commentId + " not found"));

        Like like = Like.builder()
                .userId(userId)
                .comment(comment)
                .build();

        like = likeRepository.save(like);
        log.info("User {} liked comment {}", userId, commentId);

        return likeMapper.toCommentLikeDto(like);
    }

    @Transactional
    @Override
    public void unlikePost(Long userId, Long postId) {
        validateUser(userId);

        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("Post with ID " + postId + " not found");
        }

        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new NotFoundException("Like not found");
        }

        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("User {} unliked post {}", userId, postId);
    }

    @Transactional
    @Override
    public void unlikeComment(Long userId, Long commentId) {
        validateUser(userId);

        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with ID " + commentId + " not found");
        }

        if (!likeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new NotFoundException("Like not found");
        }

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("User {} unliked comment {}", userId, commentId);
    }

    private void validateUser(long userId) {
        /*
        TODO: remove when UserService is ready
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        */
    }

    private void checkLikeDoesNotExist(Long entityId, Long userId, boolean isPost) {
        boolean exists = isPost
                ? likeRepository.existsByPostIdAndUserId(entityId, userId)
                : likeRepository.existsByCommentIdAndUserId(entityId, userId);

        if (exists) {
            throw new IllegalStateException("Like already exists for this " + (isPost ? "post" : "comment"));
        }
    }
}