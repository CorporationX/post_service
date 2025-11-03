package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DuplicateLikeException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
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

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;

    @Transactional
    public void addLikeToPost(Long postId, Long userId) {
        validateUserExist(userId);
        Post post = postRepository.getByIdOrThrow(postId);
        validateAlreadyLikedPost(postId, userId);

        Like like = Like.builder()
                .userId(userId)
                .post(post)
                .build();
        likeRepository.save(like);
        log.info("Like added to post {} by user {}", postId, userId);
    }

    @Transactional
    public void removeLikeFromPost(Long postId, Long userId) {
        validateUserExist(userId);
        Like like = likeRepository.findByPostIdAndUserIdOrThrow(postId, userId);
        validateAuthorLike(like, userId);
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Like removed from post {} by user {}", postId, userId);
    }

    @Transactional
    public void addLikeToComment(Long commentId, Long userId) {
        validateUserExist(userId);
        Comment comment = commentRepository.getByIdOrThrow(commentId);
        validateAlreadyLikedComment(commentId, userId);

        Like like = Like.builder()
                .userId(userId)
                .comment(comment)
                .build();
        likeRepository.save(like);
        log.info("Like added to comment {} by user {}", commentId, userId);
    }

    @Transactional
    public void removeLikeFromComment(Long commentId, Long userId) {
        validateUserExist(userId);
        Like like = likeRepository.findByCommentIdAndUserIdOrThrow(commentId, userId);
        validateAuthorLike(like, userId);

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Like removed from comment {} by user {}", commentId, userId);
    }

    @Transactional(readOnly = true)
    public Integer getCountLikeForPost(Long postId) {
        postRepository.getByIdOrThrow(postId);
        return likeRepository.countLikeByPost(postId);
    }

    @Transactional(readOnly = true)
    public Integer getCountLikeForComment(Long commentId) {
        commentRepository.getByIdOrThrow(commentId);
        return likeRepository.countLikeByComment(commentId);
    }

    @Transactional(readOnly = true)
    public Integer getCountLikeUserForPosts(Long userId) {
        validateUserExist(userId);
        return likeRepository.countLikeUserForPosts(userId);
    }

    @Transactional(readOnly = true)
    public Integer getCountLikeUserForComments(Long userId) {
        validateUserExist(userId);
        return likeRepository.countLikeUserForComments(userId);
    }


    private void validateUserExist(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (feign.FeignException.NotFound e) {
            log.error("Not found User with id {}", userId);
            throw new EntityNotFoundException(String.format("User with id %d not found", userId));
        } catch (EntityNotFoundException e) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException(String.format("User with id %d not found", userId));
        } catch (Exception e) {
            log.error("Error while validating user with id {}", userId, e);
            throw new RuntimeException("Service unavailable. Please try again later.");
        }
    }


    private void validateAlreadyLikedPost(Long postId, Long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()){
            throw new DuplicateLikeException(String.format("User %d already liked post %d ", userId, postId));
        }
    }

    private void validateAuthorLike(Like like, Long userId) {
        if (!Objects.equals(like.getUserId(), userId)) {
            throw new ForbiddenException("Only the author of the like can delete it");
        }
    }

    private void validateAlreadyLikedComment(Long commentId, Long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()){
            throw new DuplicateLikeException(String.format("User %d already liked comment %d ", userId, commentId));
        }
    }
}
