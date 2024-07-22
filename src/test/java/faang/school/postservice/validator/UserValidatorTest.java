package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    @DisplayName("testing userExistence")
    public void testUserExistence() {
        long userId = 1L;
        when(userServiceClient.checkUserExistence(userId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> userValidator.validateUserExistence(userId));
    }

    @Test
    @DisplayName("testing ")
    public void testFollowersExistence() {
        when(userServiceClient.doesFollowersExist(anyList())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> userValidator.validateFollowersExistence(List.of()));
    }
}