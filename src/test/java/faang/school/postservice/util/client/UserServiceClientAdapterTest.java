package faang.school.postservice.util.client;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceClientAdapterTest {

    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private UserServiceClientAdapter userServiceClientAdapter;


    @Test
    public void testCurrentUserNotFound() {
        final long currentUserId = 1L;

        when(userServiceClient.getUserById(currentUserId))
                .thenThrow(new EntityNotFoundException("User not found"));

        assertThrows(EntityNotFoundException.class, () -> userServiceClientAdapter.getUserById(currentUserId));

        verify(userServiceClient, times(1)).getUserById(currentUserId);
    }

    @Test
    public void testCurrentUserFound() {
        final long currentUserId = 1L;
        final UserDto userDto = new UserDto(currentUserId, null, null);

        when(userServiceClient.getUserById(currentUserId)).thenReturn(userDto);

        userServiceClientAdapter.getUserById(currentUserId);

        verify(userServiceClient, times(1)).getUserById(currentUserId);
    }
}
