package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeValidationService likeValidationService;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public void addLikeToPost(Long postId, Long commentId, Long currentUserId) {
        try {
            userServiceClient.getUser(currentUserId);
        } catch (FeignException.NotFound ex) {
            throw new UserNotFoundException("User not found with id: " + currentUserId);
        }

        likeValidationService.validatePostAlreadyLiked(currentUserId,postId);
        likeValidationService.validateLikeTarget(postId,commentId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        Like like = Like.builder()
                .userId(currentUserId)
                .post(post)
                .build();

        post.getLikes().add(like);
        likeRepository.save(like);
        postRepository.save(post);
    }

    @Transactional
    public void removeLikeFromPost(Long postId,Long currentUserId) {
        likeValidationService.validatePostNotBeenLiked(currentUserId,postId);
        likeRepository.deleteByUserIdAndPostId(currentUserId, postId);
    }

    @Transactional
    public void addLikeToComment(Long commentId,Long postId, Long currentUserId) {
        try {
            userServiceClient.getUser(currentUserId);
        } catch (FeignException.NotFound ex) {
            throw new UserNotFoundException("User not found with id: " + currentUserId);
        }

        likeValidationService.validateCommentAlreadyLiked(currentUserId,commentId);
        likeValidationService.validateLikeTarget(postId,commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CommentNotFoundException("Comment not found"));
        Like like = Like.builder()
                .userId(currentUserId)
                .comment(comment)
                .build();

        comment.getLikes().add(like);
        likeRepository.save(like);
        commentRepository.save(comment);
    }

    @Transactional
    public void removeLikeFromComment(Long commentId,Long currentUserId) {
        likeValidationService.validateCommentNotBeenLiked(currentUserId,commentId);
        likeRepository.deleteByUserIdAndCommentId(currentUserId, commentId);
    }
}