package faang.school.postservice.util.validator;

import faang.school.postservice.util.exception.DataValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PostControllerValidatorTest {

    private PostControllerValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PostControllerValidator();
    }

    @Test
    void validateId_IdIsGreaterThanZero_ShouldNotThrow() {
        Long id = 1L;

        Assertions.assertDoesNotThrow(() -> validator.validateId(id));
    }

    @Test
    void validateId_IdIsLowerThanOne_ShouldThrow() {
        Long id = 0L;

        DataValidationException e = Assertions.assertThrows(DataValidationException.class,
                () -> validator.validateId(id));
        Assertions.assertEquals("Id should be greater than 0", e.getMessage());
    }
}
