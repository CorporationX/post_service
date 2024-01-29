package faang.school.postservice.validator;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CommentEditDto;
import faang.school.postservice.exceptions.DataValidationException;
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
    void testValidateComment() {
        assertThrows(DataValidationException.class, () -> {
            commentValidator.validateComment(CommentDto.builder()
                    .content("  ")
                    .build());
        });
    }

    @Test
    void testValidateCommentEdit() {
        assertThrows(DataValidationException.class, () -> {
            commentValidator.validateComment(CommentEditDto.builder()
                    .content("  ")
                    .build());
        });
    }

    @Test
    void testCheckOwnerComment() {
        assertThrows(DataValidationException.class, () -> {
            commentValidator.checkOwnerComment(1L, 2L);
        });
    }
}