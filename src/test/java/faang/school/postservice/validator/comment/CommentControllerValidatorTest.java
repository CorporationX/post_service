package faang.school.postservice.validator.comment;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentControllerValidatorTest {
    private final CommentControllerValidator validator = new CommentControllerValidator();
    private CommentDto commentDto;
    private Long authorId;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto();
        authorId = 10L;
    }

    @Test
    void validateCommentDtoNotNull() {
        assertDoesNotThrow(() -> validator.validateCommentDtoNotNull(commentDto));
    }

    @Test
    void validateCommentDtoNull() {
        commentDto = null;
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateCommentDtoNotNull(commentDto));
        assertEquals("commentDto cannot be null", exception.getMessage());
    }

    @Test
    void validateCommentPostIdNotNull() {
        commentDto.setPostId(10L);
        assertDoesNotThrow(() -> validator.validateCommentPostIdNotNull(commentDto));
    }

    @Test
    void validateCommentPostIdNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateCommentPostIdNotNull(commentDto));
        assertEquals("postId cannot be null", exception.getMessage());
    }

    @Test
    void validateCommentContentNotNull() {
        commentDto.setContent("some content");
        assertDoesNotThrow(() -> validator.validateCommentContentNotNull(commentDto));
    }

    @Test
    void validateCommentContentNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateCommentContentNotNull(commentDto));
        assertEquals("content cannot be null", exception.getMessage());
    }

    @Test
    void validateCommentAuthorIdNotNull() {
        assertDoesNotThrow(() -> validator.validateCommentAuthorIdNotNull(authorId));
    }

    @Test
    void validateCommentAuthorIdNull() {
        authorId = null;
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateCommentAuthorIdNotNull(authorId));
        assertEquals("authorId cannot be null", exception.getMessage());
    }
}