package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.UserNotFoundException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.nio.charset.Charset;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserValidator userValidator;

    private final long userId = 1L;

    @BeforeEach
    void setUp() {
        reset(userServiceClient);
    }

    @Test
    void testValidateUserExists_success_whenUserExists() {
        when(userServiceClient.getUser(userId)).thenReturn(any(UserDto.class));

        assertDoesNotThrow(() -> userValidator.validateUserExists(userId));

        verify(userServiceClient, times(1)).getUser(userId);
    }

    @Test
    void testValidateUserExists_failed_whenUserNotExists() {
        when(userServiceClient.getUser(anyLong()))
                .thenThrow(new UserNotFoundException("User with ID " + userId + " not found."));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userValidator.validateUserExists(userId));

        assertEquals("User with ID " + userId + " not found.", exception.getMessage());
        verify(userServiceClient, times(1)).getUser(userId);
    }

    @Test
    void testValidateUserExists_failed_whenUserServiceNotRespond() {
        doThrow(FeignException.InternalServerError.class).when(userServiceClient).getUser(userId);
        FeignException exception = assertThrows(FeignException.class,
                () -> userServiceClient.getUser(userId));
        assertTrue(exception instanceof FeignException.InternalServerError);

        verify(userServiceClient, times(1)).getUser(userId);
    }
}
