package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comments.CommentService;
import faang.school.postservice.service.utils.PostServiceUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostServiceUtils postServiceUtils;
    private final CommentService commentService;
    private final UserServiceClient userServiceClient;

    @Transactional
    public LikeDto addLikeToPost(Long userId, Long postId) {
        checkIfLikeAuthorExists(userId);
        checkIfPostIsAlreadyLiked(userId, postId);

        Like newLikeToPost = Like.builder()
                .userId(userId)
                .post(postServiceUtils.getPost(postId))
                .build();
        return likeMapper.toDto(likeRepository.save(newLikeToPost));
    }

    @Transactional
    public boolean removeLikeFromPost(Long postId, Long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            likeRepository.deleteByPostIdAndUserId(postId, userId);
            log.info("Successfully removed like from postId: {} and userId: {}", postId, userId);
            return true;
        }
        return false;
    }

    @Transactional
    public LikeDto addLikeToComment(Long userId, Long commentId) {
        checkIfLikeAuthorExists(userId);
        checkIfCommentIsAlreadyLiked(userId, commentId);

        Like newLikeToComment = Like.builder()
                .userId(userId)
                .comment(commentService.getComment(commentId))
                .createdAt(LocalDateTime.now())
                .build();
        return likeMapper.toDto(likeRepository.save(newLikeToComment));
    }

    @Transactional
    public boolean removeLikeFromComment(Long commentId, Long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            likeRepository.deleteByCommentIdAndUserId(commentId, userId);
            log.info("Successfully removed like from commentId: {} and userId: {}", commentId, userId);
            return true;
        }
        return false;
    }

    private void checkIfLikeAuthorExists(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            log.error("Like authorId {} not found", authorId);
            throw new DataValidationException("Like author not found");
        }
        log.info("Successfully validated like authorId: {}", authorId);
    }

    private void checkIfPostIsAlreadyLiked(Long postId, Long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            log.error("This post {} is already liked by this user {}", postId, userId);
            throw new DataValidationException("Post is already liked by this user");
        }
        log.info("Post {} is not liked by this user {} and is possible to do it!", postId, userId);
    }

    private void checkIfCommentIsAlreadyLiked(Long commentId, Long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            log.error("This comment {} is already liked by this user {}", commentId, userId);
            throw new DataValidationException("Comment is already liked by this user");
        }
        log.info("Comment {} is not liked by this user {} and is possible to do it!", commentId, userId);
    }
}
