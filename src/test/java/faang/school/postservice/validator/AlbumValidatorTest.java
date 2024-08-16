package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.AlbumRejectedInAccessException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumValidatorTest {
    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AlbumValidator albumValidator;

    private Album album;
    private long albumId;
    private long authorId;
    private long notAuthorId;
    private long requesterId;

    @BeforeEach
    public void setUp() {
        authorId = 1L;
        albumId = 2L;
        notAuthorId = 3L;
        requesterId = 4L;
        album = Album.builder()
                .id(albumId)
                .title("title")
                .authorId(authorId)
                .visibility(AlbumVisibility.ONLY_AUTHOR)
                .build();
    }

    @Test
    @DisplayName("testing validateAlbumExistence method with non appropriate value")
    public void testValidateAlbumExistenceWithNonAppropriateValue() {
        when(albumRepository.existsById(albumId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> albumValidator.validateAlbumExistence(albumId));
    }

    @Test
    @DisplayName("testing validateAlbumExistence method with appropriate value")
    public void testValidateAlbumExistenceWithAppropriateValue() {
        when(albumRepository.existsById(albumId)).thenReturn(true);
        assertDoesNotThrow(() -> albumValidator.validateAlbumExistence(albumId));
    }

    @Test
    @DisplayName("testing validateAlbumTitleDoesNotDuplicatePerAuthor method with non appropriate value")
    public void testValidateAlbumTitleDoesNotDuplicatePerAuthorWithNonAppropriateValue() {
        when(albumRepository.existsByTitleAndAuthorId(album.getTitle(), authorId)).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> albumValidator.validateAlbumTitleDoesNotDuplicatePerAuthor(authorId, album.getTitle()));
    }

    @Test
    @DisplayName("testing validateAlbumTitleDoesNotDuplicatePerAuthor method with appropriate value")
    public void testValidateAlbumTitleDoesNotDuplicatePerAuthorWithAppropriateValue() {
        when(albumRepository.existsByTitleAndAuthorId(album.getTitle(), authorId)).thenReturn(false);
        assertDoesNotThrow(() -> albumValidator.validateAlbumTitleDoesNotDuplicatePerAuthor(authorId, album.getTitle()));
    }

    @Test
    @DisplayName("testing validateAlbumBelongsToAuthor method with non appropriate value")
    public void testValidateAlbumBelongsToAuthorWithNonAppropriateValue() {
        assertThrows(IllegalArgumentException.class,
                () -> albumValidator.validateAlbumBelongsToRequester(notAuthorId, album));
    }

    @Test
    @DisplayName("testing validateVisibilityToUser method with non appropriate value")
    public void testValidateVisibilityToUser() {
        assertThrows(AlbumRejectedInAccessException.class,
                () -> albumValidator.validateVisibilityToRequester(requesterId, album));
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void testIsVisibleToRequester(long requesterId, Album album, boolean expectedVisibility) {
        lenient().when(userServiceClient.getUserFollowers(anyLong()))
                .thenReturn(List.of(UserDto.builder().id(2L).build()));
        boolean visibility = albumValidator.isVisibleToRequester(requesterId, album);
        assertEquals(expectedVisibility, visibility);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of(1L, Album.builder().authorId(1L)
                        .visibility(AlbumVisibility.ONLY_AUTHOR).build(), true),

                Arguments.of(2L, Album.builder().authorId(1L)
                        .visibility(AlbumVisibility.ONLY_AUTHOR).build(), false),

                Arguments.of(2L, Album.builder().authorId(1L)
                        .visibility(AlbumVisibility.ONLY_ALLOWED_USERS).allowedUserIds(List.of(2L)).build(), true),

                Arguments.of(3L, Album.builder().authorId(1L)
                        .visibility(AlbumVisibility.ONLY_ALLOWED_USERS).allowedUserIds(List.of(2L)).build(), false),

                Arguments.of(2L, Album.builder().authorId(1L)
                        .visibility(AlbumVisibility.ONLY_FOLLOWERS).allowedUserIds(List.of(2L)).build(), true),

                Arguments.of(3L, Album.builder().authorId(1L)
                        .visibility(AlbumVisibility.ONLY_FOLLOWERS).allowedUserIds(List.of(2L)).build(), false),

                Arguments.of(1L, Album.builder().authorId(1L)
                        .visibility(AlbumVisibility.ALL).build(), true)
        );
    }

    @Test
    @DisplayName("testing validateAlbumBelongsToAuthor method with appropriate value")
    public void testValidateAlbumBelongsToAuthorWithAppropriateValue() {
        assertDoesNotThrow(() -> albumValidator.validateAlbumBelongsToAuthor(authorId, album));
    }
}