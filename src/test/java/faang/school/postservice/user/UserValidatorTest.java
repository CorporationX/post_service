package faang.school.postservice.user;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.validation.user.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserServiceClient userService;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateUserExist_InvalidUserId_ThrowsEntityNotFoundException() {
        when(userService.getUser(anyLong())).thenThrow(new EntityNotFoundException("User not found"));

        Assertions.assertThrows(EntityNotFoundException.class, () -> userValidator.validateUserExist(1L));
    }
}
