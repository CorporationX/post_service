package faang.school.postservice.util.validator;

import faang.school.postservice.util.exception.DataValidationException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostControllerValidatorTest {

    private PostControllerValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PostControllerValidator();
    }

    @Test
    void validateToPublish_IdIsGreaterThanZero_ShouldNotThrowException() {
        Long id = 1L;

        Assertions.assertDoesNotThrow(() -> validator.validateToPublish(id));
    }

    @Test
    void validateToPublish_IdIsLowerThanOne_ShouldThrowException() {
        Long id = 0L;

        DataValidationException e = Assertions.assertThrows(DataValidationException.class,
                () -> validator.validateToPublish(id));
        Assertions.assertEquals("Id should be greater than 0", e.getMessage());
    }

    @Test
    void validateToUpdate_IdIsLowerThanOne_ShouldThrowException() {
        Long id = 0L;
        String content = "content";

        DataValidationException e = Assertions.assertThrows(DataValidationException.class,
                () -> validator.validateToUpdate(id, content));
        Assertions.assertEquals("Id should be greater than 0", e.getMessage());
    }

    @Test
    void validateToUpdate_ContentIsNull_ShouldThrowException() {
        Long id = 1L;
        String content = null;

        DataValidationException e = Assertions.assertThrows(DataValidationException.class,
                () -> validator.validateToUpdate(id, content));
        Assertions.assertEquals("Content should not be empty", e.getMessage());
    }

    @Test
    void validateToUpdate_ContentIsBlank_ShouldThrowException() {
        Long id = 1L;
        String content = "  ";

        DataValidationException e = Assertions.assertThrows(DataValidationException.class,
                () -> validator.validateToUpdate(id, content));
        Assertions.assertEquals("Content should not be empty", e.getMessage());
    }

    @Test
    void validateToDelete_IdIsGreaterThanZero_ShouldNotThrowException() {
        Long id = 1L;

        Assertions.assertDoesNotThrow(() -> validator.validateToDelete(id));
    }

    @Test
    void validateToDelete_IdIsLowerThanZero_ShouldThrowException() {
        Long id = 0L;

        DataValidationException e = Assert.assertThrows(DataValidationException.class,
                () -> validator.validateToDelete(id));
        Assertions.assertEquals("Id should be greater than 0", e.getMessage());
    }

}
