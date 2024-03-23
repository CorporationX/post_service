package faang.school.postservice.validation.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumValidatorTest {

    @Mock
    private AlbumRepository albumRepository;
    @InjectMocks
    private AlbumValidator albumValidator;

    private Album album;
    private AlbumDto albumDto;

    @BeforeEach
    void setUp() {
        album = Album.builder()
                .id(1L)
                .title("Valid title")
                .description("Valid description")
                .authorId(10L)
                .build();
        albumDto = AlbumDto.builder()
                .id(album.getId())
                .title(album.getTitle())
                .description(album.getDescription())
                .authorId(album.getAuthorId())
                .build();
    }

    @Test
    void validateAlbumTitle_AlbumWithGivenTitleDoesntExist_ShouldNotThrow() {
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())).thenReturn(false);

        albumValidator.validateAlbumTitle(albumDto);

        assertAll(
                () -> verify(albumRepository, times(1)).existsByTitleAndAuthorId(
                        albumDto.getTitle(), albumDto.getAuthorId()),
                () -> assertDoesNotThrow(() -> albumValidator.validateAlbumTitle(albumDto))
        );
    }

    @Test
    void validateAlbumTitle_AlbumWithGivenTitleAlreadyExists_ShouldThrowDataValidationException() {
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> albumValidator.validateAlbumTitle(albumDto));
    }

    @Test
    void validateIfUserIsAuthor_UserIsAuthor_ShouldNotThrow() {
        assertDoesNotThrow(() -> albumValidator.validateIfUserIsAuthor(10L, album));
    }

    @Test
    void validateIfUserIsAuthor_UserIsNotTheAuthor_ShouldThrowDataValidationException() {
        assertThrows(DataValidationException.class,
                () -> albumValidator.validateIfUserIsAuthor(15L, album));
    }

    @Test
    void validateUpdatedAlbum_AuthorHasntBeenChanged_ShouldNotThrow() {
        albumDto.setTitle("Updated title");
        assertDoesNotThrow(() -> albumValidator.validateUpdatedAlbum(10L, albumDto));
    }

    @Test
    void validateUpdatedAlbum_AuthorWasChanged_ShouldThrowDataValidationException() {
        albumDto.setTitle("Updated title");
        albumDto.setAuthorId(15L);
        assertThrows(DataValidationException.class,
                () -> albumValidator.validateUpdatedAlbum(10L, albumDto));
    }
}
