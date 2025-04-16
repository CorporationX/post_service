package faang.school.postservice.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdValidatorTest {

    private AdValidator adValidator;

    @BeforeEach
    void setUp() {
        adValidator = new AdValidator();
    }

    @Test
    @DisplayName("Test: Given empty ad ID list, should throw exception")
    void testValidateAdIdsWhenListIsEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> adValidator.validateAdIds(List.of()));
        assertEquals("No expired ads found to delete", exception.getMessage());
    }

    @Test
    @DisplayName("Test: Given null ad ID list, should throw exception")
    void testValidateAdIdsWhenListIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> adValidator.validateAdIds(null));
        assertEquals("No expired ads found to delete", exception.getMessage());
    }

    @Test
    @DisplayName("Test: Given valid ad ID list, should pass validation")
    void testValidateAdIdsWhenListIsValid() {
        assertAll(
                () -> assertDoesNotThrow(() -> adValidator.validateAdIds(List.of(1L))),
                () -> assertDoesNotThrow(() -> adValidator.validateAdIds(List.of(1L, 2L, 3L)))
        );
    }

    @Test
    @DisplayName("Test: Given ad ID is zero, should throw exception")
    void testValidateAdIdWhenIdIsZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> adValidator.validateAdId(0L));
        assertEquals("Invalid ad ID", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -10, -100, -1000})
    @DisplayName("Test: Given ad ID is negative, should throw exception")
    void testValidateAdIdWhenIdIsNegative(long invalidId) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> adValidator.validateAdId(invalidId));
        assertEquals("Invalid ad ID", exception.getMessage());
    }

    @Test
    @DisplayName("Test: Given ad ID is null, should throw exception")
    void testValidateAdIdWhenIdIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> adValidator.validateAdId(null));
        assertEquals("Invalid ad ID", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 10, 100, 1000})
    @DisplayName("Test: Given ad ID is positive, should pass validation")
    void testValidateAdIdWhenIdIsValid(long validId) {
        assertDoesNotThrow(() -> adValidator.validateAdId(validId));
    }
}
