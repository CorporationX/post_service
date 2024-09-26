package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;
    private final PostService postService;

    public void validateCreate(Long postId, Comment comment) {
        validatePost(postId);
        validateComment(comment);
    }

    public void validateCommentAuthorId(Long userId, Comment comment) {
        if (comment.getAuthorId() != userId) {
            throw new ValidationException("You cannot update not yours comment");
        }
    }

    private void validatePost(Long postId) {
        try {
            postService.findPostById(postId);
        } catch (PostNotFoundException exception) {
            throw new ValidationException(exception.getMessage());
        }
    }

    private void validateComment(Comment comment) {
        userServiceClient.getUser(comment.getAuthorId());
    }
}
