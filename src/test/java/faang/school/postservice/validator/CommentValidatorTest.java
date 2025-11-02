package faang.school.postservice.validator;

import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentValidatorTest {

    @BeforeEach
    void setup() {
    }

    @Test
    void validateCommentContent_validContent_doesNotThrow() {
        assertDoesNotThrow(() -> CommentValidator.validateCommentContent("This is a valid comment."));
    }

    @Test
    void validateCommentContent_nullContent_throws() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> CommentValidator.validateCommentContent(null));
        assertEquals("Content must not be blank", ex.getMessage());
    }

    @Test
    void validateCommentContent_blankContent_throws() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> CommentValidator.validateCommentContent("    "));
        assertEquals("Content must not be blank", ex.getMessage());
    }

    @Test
    void validateCommentContent_tooLongContent_throws() {
        String longContent = "a".repeat(5000);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> CommentValidator.validateCommentContent(longContent));
        assertEquals("Content must be at most 4096 characters", ex.getMessage());
    }

    @Test
    void validateCommentOwnership_validOwnership_doesNotThrow() {
        Post post = new Post();
        post.setId(1L);

        Comment comment = new Comment();
        comment.setPost(post);

        assertDoesNotThrow(() -> CommentValidator.validateCommentOwnership(comment.getPost().getId(), 1L));
    }

    @Test
    void validateCommentOwnership_invalidOwnership_throws() {
        Post post = new Post();
        post.setId(2L);

        Comment comment = new Comment();
        comment.setPost(post);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> CommentValidator.validateCommentOwnership(comment.getPost().getId(), 1L));
        assertEquals("You can't delete someone else's comment.", ex.getMessage());
    }
}

