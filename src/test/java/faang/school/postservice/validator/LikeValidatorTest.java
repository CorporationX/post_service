package faang.school.postservice.validator;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LikeValidatorTest {

    private final LikeValidator likeValidator = new LikeValidator();

    @Test
    void validateUserExists_ShouldNotThrowExceptionWhenUserExists() {
        UserDto user = new UserDto(1L, "Test User", "test@example.com");

        assertDoesNotThrow(() -> likeValidator.validateUserExists(user));
    }

    @Test
    void validateUserExists_ShouldThrowExceptionWhenUserIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateUserExists(null));

        assertEquals("User not found or has invalid ID.", exception.getMessage());
    }

    @Test
    void validateUserExists_ShouldThrowExceptionWhenUserIdIsNull() {
        UserDto user = new UserDto(null, "Test User", "test@example.com");

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateUserExists(user));

        assertEquals("User not found or has invalid ID.", exception.getMessage());
    }

    @Test
    void validatePostExists_ShouldNotThrowExceptionWhenPostExists() {
        Post post = new Post();

        assertDoesNotThrow(() -> likeValidator.validatePostExists(post));
    }

    @Test
    void validatePostExists_ShouldThrowExceptionWhenPostIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validatePostExists(null));

        assertEquals("Post not found.", exception.getMessage());
    }

    @Test
    void validateCommentExists_ShouldNotThrowExceptionWhenCommentExists() {
        Comment comment = new Comment();

        assertDoesNotThrow(() -> likeValidator.validateCommentExists(comment));
    }

    @Test
    void validateCommentExists_ShouldThrowExceptionWhenCommentIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateCommentExists(null));

        assertEquals("Comment not found.", exception.getMessage());
    }
}