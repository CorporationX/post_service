package faang.school.postservice.validator;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CommentEditDto;
import faang.school.postservice.exceptions.DataValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentValidatorTest {
    CommentValidator commentValidator = new CommentValidator();

    @Test
    void testValidateIdIsNotNull() {
        assertThrows(DataValidationException.class, () -> {
            commentValidator.validateIdIsNotNull(0L);
            commentValidator.validateIdIsNotNull(null);
        });
    }

    @Test
    void testValidateComment() {
        assertThrows(DataValidationException.class, () -> {
            commentValidator.validateComment((CommentDto) null);
            commentValidator.validateComment(CommentDto.builder()
                    .content("  ")
                    .build());
        });
    }

    @Test
    void testValidateCommentEdit() {
        assertThrows(DataValidationException.class, () -> {
            commentValidator.validateComment((CommentEditDto) null);
            commentValidator.validateComment(CommentEditDto.builder()
                    .content("  ")
                    .build());
        });
    }

    @Test
    void testValidateOwnerComment() {
        assertThrows(DataValidationException.class, () -> {
            commentValidator.validateOwnerComment(1L, 2L);
        });
    }
}