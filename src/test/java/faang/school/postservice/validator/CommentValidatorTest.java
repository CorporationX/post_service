package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentValidatorTest {
    CommentValidator commentValidator = new CommentValidator();

    @Test
    void testValidateIdIsNotLessOne() {
        assertThrows(DataValidationException.class, () -> {
            commentValidator.validateIdIsNotLessOne(0L);
        });
    }

    @Test
    void testValidateContent() {
        assertThrows(DataValidationException.class, () -> {
            commentValidator.validateContent("   ");
        });
    }

    @Test
    void testCheckOwnerComment() {
        assertThrows(DataValidationException.class, () -> {
            commentValidator.checkOwnerComment(1L, 2L);
        });
    }
}