package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentServiceHandler {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public void userExistsByIdValidation(long authorId) {
        if (userServiceClient.getUser(authorId) == null) {
            throw new DataValidationException("Author with ID: " + authorId + " not found.");
        }
    }

    public void commentExistsByIdValidation(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new DataValidationException("Comment with ID: " + commentId + " not found.");
        }
    }

    public void postExistsByIdValidation(long postId) {
        if (!postRepository.existsById(postId)) {
            throw new DataValidationException("Post with ID: " + postId + " not found.");
        }
    }

    public void editCommentByAuthorValidation(UserDto user, Comment updatedComment) {
        if (!user.getId().equals(updatedComment.getAuthorId())) {
            throw new DataValidationException("Only Author can edit comment");
        }
    }
}
