package faang.school.postservice.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ControllerValidatorTest {
    private static final String MESSAGE_INVALID_ID = "Message invalid id";
    private static final long INVALID_ID = -1L;
    private static final String MESSAGE_INVALID_DTO = "Dto is null";
    private ControllerValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new ControllerValidator();
    }

    @Test
    public void testInvalidId() {
        //Assert
        assertEquals(MESSAGE_INVALID_ID,
                assertThrows(RuntimeException.class,
                        () -> validator.validateId(INVALID_ID, MESSAGE_INVALID_ID)).getMessage());
    }

    @Test
    public void testInvalidDto() {
        //Assert
        assertEquals(MESSAGE_INVALID_DTO,
                assertThrows(RuntimeException.class,
                        () -> validator.validateDto(null)).getMessage());
    }
}