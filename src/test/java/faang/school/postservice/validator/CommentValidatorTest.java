package faang.school.postservice.validator;

import faang.school.postservice.exeption.ValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentValidatorTest {

    private CommentValidator validator;

    @BeforeEach
    void setup() {
        validator = new CommentValidator();
    }

    @Test
    void validateCommentContent_validContent_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validateCommentContent("This is a valid comment."));
    }

    @Test
    void validateCommentContent_nullContent_throws() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validateCommentContent(null));
        assertEquals("Content must not be blank", ex.getMessage());
    }

    @Test
    void validateCommentContent_blankContent_throws() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validateCommentContent("    "));
        assertEquals("Content must not be blank", ex.getMessage());
    }

    @Test
    void validateCommentContent_tooLongContent_throws() {
        String longContent = "a".repeat(5000);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validateCommentContent(longContent));
        assertEquals("Content must be at most 4096 characters", ex.getMessage());
    }

    @Test
    void validateCommentOwnership_validOwnership_doesNotThrow() {
        Post post = new Post();
        post.setId(1L);

        Comment comment = new Comment();
        comment.setPost(post);

        assertDoesNotThrow(() -> validator.validateCommentOwnership(comment, 1L));
    }

    @Test
    void validateCommentOwnership_invalidOwnership_throws() {
        Post post = new Post();
        post.setId(2L);

        Comment comment = new Comment();
        comment.setPost(post);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validateCommentOwnership(comment, 1L));
        assertEquals("Comment does not belong to this post", ex.getMessage());
    }
}

