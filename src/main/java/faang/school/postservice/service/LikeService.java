package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.exception.AlreadyExistsException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;

    private final UserServiceClient userServiceClient;

    private final PostService postService;
    private final CommentService commentService;
    private final LikeEventPublisher likeEventPublisher;

    @Transactional
    public LikeResponseDto addLikeToPost(long userId, long postId) {
        Post post = postService.getById(postId);
        checkUserExistence(userId);
        checkIfPostAlreadyLiked(userId, postId);

        Like like = Like.builder()
                .userId(userId)
                .post(post)
                .build();

        like = likeRepository.save(like);

        likeEventPublisher.publish(likeMapper.toPostLikeEvent(like));

        return likeMapper.toResponseDto(like);
    }

    @Transactional
    public LikeResponseDto addLikeToComment(long userId, long commentId) {
        Comment comment = commentService.getById(commentId);
        checkUserExistence(userId);
        checkIfCommentAlreadyLiked(userId, commentId);

        Like like = Like.builder()
                .userId(userId)
                .comment(comment)
                .build();

        like = likeRepository.save(like);
        return likeMapper.toResponseDto(like);
    }

    @Transactional(readOnly = true)
    public Like getByPostIdAndUserId(long postId, long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Like from user id=%d to post id=%d not found",
                        userId, postId)));
    }

    @Transactional(readOnly = true)
    public Like getByCommentIdAndUserId(long commentId, long userId) {
        return likeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Like from user id=%d to comment id=%d not found",
                        userId, commentId)));
    }

    @Transactional
    public LikeResponseDto removeLikeFromPost(long userId, long postId) {
        Like deletedLike = getByPostIdAndUserId(postId, userId);
        likeRepository.delete(deletedLike);
        return likeMapper.toResponseDto(deletedLike);
    }

    @Transactional
    public LikeResponseDto removeLikeFromComment(long userId, long commentId) {
        Like deletedLike = getByCommentIdAndUserId(commentId, userId);
        likeRepository.delete(deletedLike);
        return likeMapper.toResponseDto(deletedLike);
    }

    private void checkUserExistence(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            log.error("Feign client thrown exception:", e);
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
    }

    private void checkIfPostAlreadyLiked(long userId, long postId) {
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new AlreadyExistsException(String.format(
                    "Like from user id=%d to post id=%d already exists",
                    userId, postId));
        }
    }

    private void checkIfCommentAlreadyLiked(long userId, long commentId) {
        if (likeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new AlreadyExistsException(String.format(
                    "Like from user id=%d to comment id=%d already exists",
                    userId, commentId));
        }
    }

}
