package faang.school.postservice.validation.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CommentValidation {
    public static final int MAX_LENGTH_CONTENT = 4096;

    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    public void validateLengthContentComment(Comment comment) {
        String content = comment.getContent();
        if (content.isEmpty()) {
            throw new DataValidationException("the length of the comment is empty");
        }
        if (content.length() > MAX_LENGTH_CONTENT) {
            throw new DataValidationException(
                    "the length of the comment is more than %d characters".formatted(MAX_LENGTH_CONTENT));
        }
    }

    public void validateAuthorExists(Comment comment) {
        Long authorId = comment.getAuthorId();
        userServiceClient.getUser(authorId);
    }

    public void validateCommentEqualsUpdateComment(Comment comment, Comment updateComment) {
        String contentComment = comment.getContent();
        String contentUpdateComment = updateComment.getContent();
        if (Objects.equals(contentComment, contentUpdateComment)) {
            throw new DataValidationException("the comment has not been updated");
        }
    }

    public void validatePostExists(Comment comment) {
        Post post = comment.getPost();
        if (null == post) {
            throw new DataValidationException("the comment is not linked to the post");
        }
        validatePostExistsById(post.getId());
    }

    public void validatePostExistsById(long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(("the post does not exists %d".formatted(postId)));
        }
    }

    public void validateCommentExists(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(("the comment does not exists %d".formatted(commentId)));
        }
    }
}
