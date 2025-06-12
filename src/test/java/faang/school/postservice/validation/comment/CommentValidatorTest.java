package faang.school.postservice.validation.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserClientResponseDto;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentValidator commentValidator;

    private static final long USER_ID = 1L;

    @Test
    public void testValidateCommentAuthor_successfully() {
        when(userServiceClient.getUserById(USER_ID)).thenAnswer(invocation -> {
            UserClientResponseDto userDto = new UserClientResponseDto();
            userDto.setId(1L);
            return userDto;
        });

        assertDoesNotThrow(() -> commentValidator.validateCommentAuthor(USER_ID));
    }

    @Test
    public void testValidateCommentAuthor_userNotFound() {
        when(userServiceClient.getUserById(USER_ID)).thenThrow(FeignException.class);

        assertThrows(EntityNotFoundException.class, () -> commentValidator.validateCommentAuthor(USER_ID));
    }
}
