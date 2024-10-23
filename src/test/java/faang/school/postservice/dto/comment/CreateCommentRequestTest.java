package faang.school.postservice.dto.comment;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class CreateCommentRequestTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("successful testing of a CreateCommentRequestTest with invalid parameters")
    void whenDtoIsNotValidThenTrue() {
        CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
                .content("")
                .authorId(1L)
                .build();

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(createCommentRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("successful testing of a CreateCommentRequestTest with valid parameters")
    void whenDtoIsValidThenTrue() {
        CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
                .content("test")
                .authorId(1L)
                .build();

        Set<ConstraintViolation<CreateCommentRequest>> violations = validator.validate(createCommentRequest);
        assertTrue(violations.isEmpty());
    }
}