package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentValidatorTest {

    private CommentValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CommentValidator();
    }

    @Test
    void validateAuthor_whenAuthorMatches_noException() {
        Comment comment = new Comment();
        comment.setAuthorId(10L);

        assertDoesNotThrow(() -> validator.validateAuthor(comment, 10L));
    }

    @Test
    void validateAuthor_whenAuthorDoesNotMatch_throwsException() {
        Comment comment = new Comment();
        comment.setAuthorId(10L);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> validator.validateAuthor(comment, 20L));

        assertEquals("Only the author can modify or delete this comment.", ex.getMessage());
    }

    @Test
    void validateCommentCreate_validDto_noException() {
        CommentDto dto = new CommentDto();
        dto.setContent("Valid comment");
        dto.setAuthorId(1L);
        dto.setPostId(1L);

        assertDoesNotThrow(() -> validator.validateCommentCreate(dto));
    }

    @Test
    void validateCommentCreate_emptyContent_throwsException() {
        CommentDto dto = new CommentDto();
        dto.setContent("   ");
        dto.setAuthorId(1L);
        dto.setPostId(1L);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> validator.validateCommentCreate(dto));

        assertEquals("Comment content must not be empty.", ex.getMessage());
    }

    @Test
    void validateCommentCreate_tooLongContent_throwsException() {
        CommentDto dto = new CommentDto();
        dto.setContent("a".repeat(5000));  // длина > 4096
        dto.setAuthorId(1L);
        dto.setPostId(1L);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> validator.validateCommentCreate(dto));

        assertEquals("Comment content must be less than 4096 characters.", ex.getMessage());
    }

    @Test
    void validateCommentCreate_nullAuthorId_throwsException() {
        CommentDto dto = new CommentDto();
        dto.setContent("Valid");
        dto.setAuthorId(null);
        dto.setPostId(1L);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> validator.validateCommentCreate(dto));

        assertEquals("Comment must have an author.", ex.getMessage());
    }

    @Test
    void validateCommentCreate_nullPostId_throwsException() {
        CommentDto dto = new CommentDto();
        dto.setContent("Valid");
        dto.setAuthorId(1L);
        dto.setPostId(null);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> validator.validateCommentCreate(dto));

        assertEquals("Comment must be linked to a post.", ex.getMessage());
    }

    @Test
    void validateCommentUpdate_validContent_noException() {
        CommentDto dto = new CommentDto();
        dto.setContent("Updated content");

        assertDoesNotThrow(() -> validator.validateCommentUpdate(dto));
    }

    @Test
    void validateCommentUpdate_emptyContent_throwsException() {
        CommentDto dto = new CommentDto();
        dto.setContent(" ");

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> validator.validateCommentUpdate(dto));

        assertEquals("Comment content must not be empty.", ex.getMessage());
    }

    @Test
    void validateCommentUpdate_tooLongContent_throwsException() {
        CommentDto dto = new CommentDto();
        dto.setContent("a".repeat(5000));

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> validator.validateCommentUpdate(dto));

        assertEquals("Comment content must be less than 4096 characters.", ex.getMessage());
    }
}
