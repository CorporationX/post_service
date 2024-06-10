package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumValidatorTest {

    @InjectMocks
    private AlbumValidator validator;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserServiceClient userServiceClient;

    private AlbumDto albumDto;
    private long userId;

    @BeforeEach
    void setUp() {
        albumDto = AlbumDto.builder()
                .authorId(1L)
                .title("Title")
                .description("Desc")
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();
        userId = 100L;
    }

    @Test
    void testValidateUniqueTitle() {
        when(albumRepository.existsByTitleAndAuthorId("Title", 1L)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateUniqueTitle(albumDto));
        verify(albumRepository, times(1)).existsByTitleAndAuthorId("Title", 1L);
    }

    @Test
    void testValidateNonUniqueTitle() {
        when(albumRepository.existsByTitleAndAuthorId("Title", 1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> validator.validateUniqueTitle(albumDto));
    }

    @Test
    void testValidateAlbumAuthorThrowException() {
        Album album = Album.builder().authorId(99L).build();

        assertThrows(IllegalArgumentException.class,
                () -> validator.validateAlbumAuthor(userId, album));
    }

    @Test
    void testValidateAlbumAuthor() {
        Album album = Album.builder().authorId(100L).build();

        assertDoesNotThrow(() -> validator.validateAlbumAuthor(userId, album));
    }

    @Test
    void testValidateUserThrowException() {
        when(userServiceClient.getUser(userId)).thenThrow(RuntimeException.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateUser(userId));
        assertEquals("The user is not in the system", exception.getMessage());
    }

    @Test
    void testValidateUser() {
        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(1L, "qwe", "qwe"));
        assertDoesNotThrow(() -> validator.validateUser(userId));
    }

    @Test
    void testValidateChangeAuthorThrowException() {
        Album album = Album.builder().authorId(2L).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateChangeAuthor(album, albumDto));

        assertEquals("You can change only your album but you can't change author of the album",
                exception.getMessage());
    }

    @Test
    void testValidateChangeAuthor() {
        Album album = Album.builder().authorId(1L).build();

        assertDoesNotThrow(() -> validator.validateChangeAuthor(album, albumDto));
    }
}
