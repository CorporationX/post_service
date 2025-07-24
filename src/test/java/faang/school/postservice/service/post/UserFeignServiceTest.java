package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.FeignClientException;
import faang.school.postservice.exception.handler.FeignExceptionHandler;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFeignServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private FeignExceptionHandler feignExceptionHandler;

    @InjectMocks
    private UserFeignService userFeignService;

    private final Long userId = 1L;
    private final String username = "Bob";
    private final String userEmail = "email@email.com";

    @Test
    void getUserOrFailReturnsUserWhenOK() {

        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, username, userEmail));

        UserDto result = userFeignService.getUserOrFail(userId);

        verify(userServiceClient).getUser(userId);
        assertEquals(userId, result.id());
        assertEquals(username, result.username());
    }

    @Test
    void getUserOrFailThrowsHandledFeignException() {
        FeignException feignException = new FeignException.InternalServerError(
                "Internal error",
                Request.create(Request.HttpMethod.GET, "/users/42", Map.of(), null, null, null),
                null,
                null
        );
        when(userServiceClient.getUser(userId)).thenThrow(feignException);
        when(feignExceptionHandler.handleFeignException(any(), any(), any())).thenThrow(new FeignClientException("Error"));

        assertThrows(FeignClientException.class, () ->  userFeignService.getUserOrFail(userId));
    }



}