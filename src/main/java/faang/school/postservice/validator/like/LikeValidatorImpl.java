package faang.school.postservice.validator.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.CommentLike;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostLike;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeValidatorImpl implements LikeValidator {

    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public Post validateAndGetPostToLike(long userId, long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post with postId:" + postId + " not found"));

        boolean isLiked = post.getLikes().stream()
                .map(PostLike::getUserId)
                .anyMatch(likedUserId -> likedUserId == userId);

        if (isLiked) {
            throw new DataValidationException("user with userId:" + userId + " can't like post with postId:" + post.getId() + " two times");
        }

        return post;
    }

    @Override
    @Transactional(readOnly = true)
    public Comment validateAndGetCommentToLike(long userId, long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment with commentId:" + commentId + " not found"));

        boolean isLiked = comment.getLikes().stream()
                .map(CommentLike::getUserId)
                .anyMatch(likedUserId -> likedUserId == userId);

        if (isLiked) {
            throw new DataValidationException("user with userId:" + userId + " can't like comment with commentId:" + comment.getId() + " two times");
        }

        return comment;
    }

    public void validateUserExistence(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new NotFoundException("can't find user with userId:" + userId);
        }
    }
}
