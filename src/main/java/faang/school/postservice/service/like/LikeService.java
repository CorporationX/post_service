package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DuplicateLikeException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.like.LikeEventPublisher;
import faang.school.postservice.publisher.like.UnlikeEventPublisher;
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
    private final UserContext userContext;
    private final LikeEventPublisher likeEventPublisher;
    private final UnlikeEventPublisher unlikeEventPublisher;

    @Transactional
    public LikeDto addLikeToPost(Long postId) {
        Long userId = userContext.getUserId();
        validateUserExist(userId);
        Post post = postRepository.getByIdOrThrow(postId);
        Long postAuthorId = post.getAuthorId();
        validateAlreadyLikedPost(postId, userId);

        Like like = Like.builder()
                .userId(userId)
                .post(post)
                .build();
        likeRepository.save(like);
        log.info("Like added to post {} by user {}", postId, userId);

        try {
            likeEventPublisher.publishLikeEvent(postAuthorId, userId, postId);
            log.info("Like event published for post {} by user {}", postId, userId);
        } catch (Exception e) {
            log.error("Failed to publish like event for post {} by user {}", postId, userId, e);
        }

        return LikeMapper.toDtoWithPost(like);
    }

    @Transactional
    public LikeDto removeLikeFromPost(Long postId) {
        Long userId = userContext.getUserId();
        validateUserExist(userId);
        Post post = postRepository.getByIdOrThrow(postId);
        Long postAuthorId = post.getAuthorId();
        Like like = likeRepository.findByPostIdAndUserIdOrThrow(postId, userId);
        validateAuthorLike(like, userId);
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Like removed from post {} by user {}", postId, userId);

        try {
            unlikeEventPublisher.publishUnlikeEvent(postAuthorId, userId, postId);
            log.info("Unlike event published for post {} by user {}", postId, userId);
        } catch (Exception e) {
            log.error("Failed to publish unlike event for post {} by user {}", postId, userId, e);
        }
        return LikeMapper.toDtoWithPost(like);
    }

    @Transactional
    public LikeDto addLikeToComment(Long commentId) {
        Long userId = userContext.getUserId();
        validateUserExist(userId);
        Comment comment = commentRepository.findByIdOrThrow(commentId);
        validateAlreadyLikedComment(commentId, userId);

        Like like = Like.builder()
                .userId(userId)
                .comment(comment)
                .build();
        likeRepository.save(like);
        log.info("Like added to comment {} by user {}", commentId, userId);
        return LikeMapper.toDtoWithComment(like);
    }

    @Transactional
    public LikeDto removeLikeFromComment(Long commentId) {
        Long userId = userContext.getUserId();
        validateUserExist(userId);
        Like like = likeRepository.findByCommentIdAndUserIdOrThrow(commentId, userId);
        validateAuthorLike(like, userId);

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Like removed from comment {} by user {}", commentId, userId);
        return LikeMapper.toDtoWithComment(like);
    }

    private void validateUserExist(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        if (userDto == null || !Objects.equals(userId, userDto.id())) {
            log.error("User validation failed for id {}", userId);
            throw new EntityNotFoundException(String.format("User with id %d not found", userId));
        }
    }

    private void validateAlreadyLikedPost(Long postId, Long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new DuplicateLikeException(String.format("User %d already liked post %d ", userId, postId));
        }
    }

    private void validateAuthorLike(Like like, Long userId) {
        if (!Objects.equals(like.getUserId(), userId)) {
            throw new ForbiddenException("Only the author of the like can delete it");
        }
    }

    private void validateAlreadyLikedComment(Long commentId, Long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new DuplicateLikeException(String.format("User %d already liked comment %d ", userId, commentId));
        }
    }
}
