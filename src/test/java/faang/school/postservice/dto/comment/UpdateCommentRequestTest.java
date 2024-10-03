package faang.school.postservice.dto.comment;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UpdateCommentRequestTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("successful testing of a UpdateCommentRequestTest with invalid parameters")
    void whenDtoIsNotValidThenTrue() {
        UpdateCommentRequest updateCommentRequest = UpdateCommentRequest.builder()
                .content("")
                .authorId(1L)
                .build();

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(updateCommentRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("successful testing of a UpdateCommentRequestTest with valid parameters")
    void whenDtoIsValidThenTrue() {
        UpdateCommentRequest updateCommentRequest = UpdateCommentRequest.builder()
                .content("test")
                .authorId(1L)
                .build();

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(updateCommentRequest);
        assertTrue(violations.isEmpty());
    }

/*   не понимаю почему не работает, можешь подсказать что не так делаю?

 @ParameterizedTest
    @ValueSource(strings = {"", "null"})
    @DisplayName("test")
    void test(String content) {

        UpdateCommentRequest updateCommentRequest = UpdateCommentRequest.builder()
                .content(content)
                .authorId(1L)
                .build();

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(updateCommentRequest);
        assertFalse(violations.isEmpty());
    }*/
}