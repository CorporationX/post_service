package faang.school.postservice.service;

import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentValidatorTest {

    private CommentValidator validator;
    private Comment comment;

    @BeforeEach
    void setUp() {
        validator = new CommentValidator();

        comment = Comment.builder()
                .id(1L)
                .authorId(42L)
                .content("Test content")
                .build();
    }

    @Test
    void validateAuthor_shouldPassIfAuthorMatches() {
        assertDoesNotThrow(() -> validator.validateAuthor(comment, 42L));
    }

    @Test
    void validateAuthor_shouldThrowIfAuthorMismatch() {
        CommentValidationException exception = assertThrows(
                CommentValidationException.class,
                () -> validator.validateAuthor(comment, 99L)
        );

        assertEquals("Only the author can modify or delete this comment.", exception.getMessage());
    }
}
