package faang.school.postservice.validation.comment;

import faang.school.postservice.exception.DataValidationException;
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

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    public void validateLengthContentComment(String content) {
        if (content.isEmpty()) {
            throw new DataValidationException("the length of the comment is empty");
        }
        if (content.length() > MAX_LENGTH_CONTENT) {
            throw new DataValidationException(
                    "the length of the comment is more than %d characters".formatted(MAX_LENGTH_CONTENT));
        }
    }

    public void checkAuthorEqualsUser(Long authorId, Long userId) {
        if (null == authorId || null == userId) {
            throw new DataValidationException("User cannot be Null");
        }
        if (!Objects.equals(authorId, userId)) {
            throw new DataValidationException("userId and authorId must match");
        }
    }

    public void checkContentNotEquals(String oldContent, String newContent) {
        if (null == oldContent || null == newContent) {
            throw new DataValidationException("content cannot be Null");
        }
        if (Objects.equals(oldContent, newContent)) {
            throw new DataValidationException("the comment has not been updated");
        }
    }

    public void validatePostExists(Long postId) {
        if (null == postId) {
            throw new DataValidationException("post cannot be Null");
        }
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
