package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    @DisplayName("testing userExistence with non appropriate value")
    public void testUserExistenceWithNonAppropriateValue() {
        long userId = 1L;
        when(userServiceClient.existsById(userId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> userValidator.validateUserExistence(userId));
    }

    @Test
    @DisplayName("testing userExistence with appropriate value")
    public void testUserExistenceWithAppropriateValue() {
        long userId = 1L;
        when(userServiceClient.existsById(userId)).thenReturn(true);
        assertDoesNotThrow(() -> userValidator.validateUserExistence(userId));
    }
}