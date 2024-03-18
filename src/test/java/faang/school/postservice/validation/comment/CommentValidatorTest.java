package faang.school.postservice.validation.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {

    @InjectMocks
    private CommentValidator commentValidator;

    @Test
    void validateCommentFields_NullContent_ThrowsDataValidationException() {
        assertThrows(DataValidationException.class, ()
                -> commentValidator.validateCommentFields(getCommentDtoNullContent()));
    }

    @Test
    void validateCommentFields_BlankContent_ThrowsDataValidationException() {
        assertThrows(DataValidationException.class, ()
                -> commentValidator.validateCommentFields(getCommentDtoBlankContent()));
    }

    @Test
    void validateCommentFields_InvalidContentLength_ThrowsDataValidationException() {
        assertThrows(DataValidationException.class, ()
                -> commentValidator.validateCommentFields(getCommentDtoInvalidContentLength()));
    }

    @Test
    void validateCommentFields_ValidArgs_DoesNotThrowsException() {
        assertDoesNotThrow(() -> commentValidator.validateCommentFields(getCommentDto()));
    }

    private CommentDto getCommentDto() {
        return CommentDto.builder()
                .content("content")
                .build();
    }

    private CommentDto getCommentDtoNullContent() {
        return CommentDto.builder().build();
    }

    private CommentDto getCommentDtoBlankContent() {
        return CommentDto.builder()
                .content("      ")
                .build();
    }

    private CommentDto getCommentDtoInvalidContentLength() {
        StringBuilder string = new StringBuilder();
        string.append("a".repeat(4097));
        return CommentDto.builder()
                .content(string.toString())
                .build();
    }


}
