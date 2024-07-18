package faang.school.postservice.validator;

import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumValidatorTest {
    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private AlbumValidator albumValidator;

    private Album album;
    private long authorId;
    private long notAuthorId;
    private long albumId;

    @BeforeEach
    public void setUp() {
        authorId = 1L;
        albumId = 2L;
        notAuthorId = 3L;
        album = Album.builder()
                .id(albumId)
                .title("title")
                .authorId(authorId)
                .build();
    }

    @Test
    @DisplayName("testing validateAlbumTitleDoesNotDuplicatePerAuthor method with appropriate value")
    public void testValidateAlbumTitleDoesNotDuplicatePerAuthor() {
        when(albumRepository.existsByTitleAndAuthorId(album.getTitle(), authorId)).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> albumValidator.validateAlbumTitleDoesNotDuplicatePerAuthor(authorId, album.getTitle()));
    }

    @Test
    @DisplayName("testing validateAlbumExistence method with non appropriate value")
    public void testValidateAlbumExistence() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> albumValidator.validateAlbumExistence(albumId));
    }

    @Test
    @DisplayName("testing validateAlbumBelongsToAuthor method with non appropriate value")
    public void testValidateAlbumBelongsToAuthor() {
        assertThrows(IllegalArgumentException.class,
                () -> albumValidator.validateAlbumBelongsToAuthor(notAuthorId, album));
    }
}