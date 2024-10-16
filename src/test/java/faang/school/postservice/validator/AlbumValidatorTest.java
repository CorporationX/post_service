package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class AlbumValidatorTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AlbumValidator albumValidator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateUserIsAuthor_UserIsNotAuthor_ShouldThrowException() {
        long userId = 1L;
        long authorId = 2L;

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> albumValidator.validateUserIsAuthor(userId, authorId));

        assertEquals("The user cannot modify someone else's album", thrown.getMessage());
    }

    @Test
    public void testValidateUserIsAuthor_UserIsAuthor_ShouldPass() {
        long userId = 1L;
        long authorId = 1L;

        assertDoesNotThrow(() -> albumValidator.validateUserIsAuthor(userId, authorId));
    }

    @Test
    public void testValidateAlbumNotExists_AlbumAlreadyExists_ShouldThrowException() {
        String title = "Test Album";
        long userId = 1L;

        when(albumRepository.existsByTitleAndAuthorId(title, userId)).thenReturn(true);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> albumValidator.validateAlbumNotExists(title, userId));

        assertEquals(String.format("Used id = %d already has album with name %s", userId, title), thrown.getMessage());
    }

    @Test
    public void testValidateAlbumNotExists_AlbumDoesNotExist_ShouldPass() {
        String title = "Test Album";
        long userId = 1L;

        when(albumRepository.existsByTitleAndAuthorId(title, userId)).thenReturn(false);

        assertDoesNotThrow(() -> albumValidator.validateAlbumNotExists(title, userId));
    }

    @Test
    public void testValidateUser_UserIdIsZeroOrNegative_ShouldThrowException() {
        long invalidUserId = -1L;

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> albumValidator.validateUser(invalidUserId));

        assertEquals(String.format("User's id can't be equal %d", invalidUserId), thrown.getMessage());
    }

    @Test
    public void testValidateUser_UserDoesNotExist_ShouldThrowException() {
        long userId = 1L;

        when(userServiceClient.getUser(userId)).thenReturn(null);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> albumValidator.validateUser(userId));

        assertEquals(String.format("The user must exist in the system, userId = %d", userId), thrown.getMessage());
    }

    @Test
    public void testValidateUser_UserExists_ShouldPass() {
        long userId = 1L;
        UserDto userDto = new UserDto(userId, "John", "john@gmail.com");

        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        assertDoesNotThrow(() -> albumValidator.validateUser(userId));
    }
}

