package faang.school.postservice.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostCorrectionValidatorTest {

    private PostCorrectionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PostCorrectionValidator();
    }

    @Test
    void isTextValidShouldReturnFalseWhenTextIsNull() {
        assertFalse(validator.isTextValid(null));
    }

    @Test
    void isTextValidShouldReturnFalseWhenTextIsBlank() {
        assertFalse(validator.isTextValid("   "));
    }

    @Test
    void isTextValidShouldReturnTrueWhenTextIsNotBlank() {
        assertTrue(validator.isTextValid("Hello world"));
    }

    @Test
    void isCorrectionValidShouldReturnFalseWhenTextIsNull() {
        assertFalse(validator.isCorrectionValid(null));
    }

    @Test
    void isCorrectionValidShouldReturnFalseWhenTextIsBlank() {
        assertFalse(validator.isCorrectionValid("   "));
    }

    @Test
    void isCorrectionValidShouldReturnTrueWhenTextIsNotBlank() {
        assertTrue(validator.isCorrectionValid("Corrected text"));
    }

    @Test
    void isCorrectionDifferentShouldReturnFalseWhenTextIsSame() {
        assertFalse(validator.isCorrectionDifferent("Text", "Text"));
    }

    @Test
    void isCorrectionDifferentShouldReturnTrueWhenTextIsDifferent() {
        assertTrue(validator.isCorrectionDifferent("Wrong text", "Corrected text"));
    }
}