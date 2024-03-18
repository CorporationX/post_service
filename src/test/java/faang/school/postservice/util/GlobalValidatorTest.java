package faang.school.postservice.util;

import faang.school.postservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GlobalValidatorTest {

    @Test
    void validateOptional_NullValue_ThrowsEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, ()
                -> GlobalValidator.validateOptional(Optional.ofNullable(null), "Entity not found"));
    }

    @Test
    void validateOptional_ValidValue_ThrowsEntityNotFoundException() {
        assertDoesNotThrow(() -> GlobalValidator.validateOptional(Optional.ofNullable(new Object()), "Entity not found"));
    }
}
