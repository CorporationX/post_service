package faang.school.postservice.validation.user;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserServiceClient userService;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateUserExist_InvalidUserId_ThrowsEntityNotFoundException() {
        when(userService.getUser(anyLong())).thenThrow(new FeignException.InternalServerError("message", getRequest(), new byte[]{}, new HashMap<>()));

        assertThrows(EntityNotFoundException.class, () -> userValidator.validateUserExist(1L));
        verify(userService, times(1)).getUser(anyLong());
    }

    @Test
    void validateUserExist_ValidArgs_DoesNotThrowException() {
        assertDoesNotThrow(() -> userValidator.validateUserExist(1L));
        verify(userService, times(1)).getUser(anyLong());
    }

    private Request getRequest() {
        return Request.create(Request.HttpMethod.GET, "http://example.com", new HashMap<>(), null, new RequestTemplate());
    }

}
