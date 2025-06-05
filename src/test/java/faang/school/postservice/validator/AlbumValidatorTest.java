package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.mapper.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumValidatorTest {

    @InjectMocks
    AlbumValidator albumValidator;

    @Mock
    AlbumRepository albumRepository;

    @Mock
    UserServiceClient userServiceClient;

    @Spy
    private AlbumMapper albumMapper = new AlbumMapperImpl();

    private Album album;

    @BeforeEach
    void setUp() {
        album = Album.builder()
                .id(1L)
                .title("Test Title")
                .authorId(10L)
                .build();
    }

    @Test
    void validateUniqueTitle_ShouldThrowException_WhenTitleExists() {
        when(albumRepository.existsByTitleAndAuthorId("Test Title", 10L)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> albumValidator.validateUniqueTitle(album));
    }

    @Test
    void validateUniqueTitle_ShouldDoNothing_WhenTitleIsUnique() {
        when(albumRepository.existsByTitleAndAuthorId("Test Title", 10L)).thenReturn(false);

        assertDoesNotThrow(() -> albumValidator.validateUniqueTitle(album));
    }

    @Test
    void validateAddPostToAlbum_ShouldThrowException_WhenUserIsNotAuthor() {
        Album album = Album.builder()
                .authorId(10L)
                .posts(List.of())
                .build();

        long userId = 99L;
        long postId = 5L;

        assertThrows(DataValidationException.class, () -> {
            albumValidator.validateAddPostToAlbum(album, postId, userId);
        });
    }

    @Test
    void validateAddPostToAlbum_ShouldThrowException_WhenPostAlreadyExistsInAlbum() {
        Post existingPost = new Post();
        existingPost.setId(5L);

        Album album = Album.builder()
                .authorId(10L)
                .posts(List.of(existingPost))
                .build();

        long userId = 10L;

        assertThrows(DataValidationException.class, () -> {
            albumValidator.validateAddPostToAlbum(album, 5L, userId);
        });
    }

    @Test
    void validateAddPostToAlbum_Success() {
        Album album = Album.builder()
                .authorId(10L)
                .posts(List.of())
                .build();

        long userId = 10L;

        assertDoesNotThrow(() -> {
            albumValidator.validateAddPostToAlbum(album, 5L, userId);
        });
    }

    @Test
    void validateRemovePostFromAlbum_shouldPass_whenUserIsAuthorAndPostExists() {
        Album album = Album.builder()
                .authorId(1L)
                .posts(List.of(Post.builder().id(100L).build()))
                .build();

        assertDoesNotThrow(() -> albumValidator.validateRemovePostFromAlbum(album, 100L, 1L));
    }

    @Test
    void validateRemovePostFromAlbum_shouldThrowException_whenUserIsNotAuthor() {
        Album album = Album.builder()
                .authorId(1L)
                .posts(List.of(Post.builder().id(100L).build()))
                .build();

        long otherUserId = 2L;

        assertThrows(DataValidationException.class, () ->
                albumValidator.validateRemovePostFromAlbum(album, 100L, otherUserId));
    }

    @Test
    void validateRemovePostFromAlbum_shouldThrowException_whenPostDoesNotExist() {
        Album album = Album.builder()
                .authorId(1L)
                .posts(List.of(Post.builder().id(200L).build()))
                .build();

        assertThrows(DataValidationException.class, () ->
                albumValidator.validateRemovePostFromAlbum(album, 100L, 1L));
    }

    @Test
    void validateAddAlbumToFavorite_throwsException_thenUserIsNotAuthor() {
        Album album = Album.builder()
                .id(1L)
                .authorId(99L)
                .build();

        long userId = 10L;

        assertThrows(DataValidationException.class, () ->
                albumValidator.validateAddAlbumToFavorite(album, userId));
    }

    @Test
    void validateRemoveAlbumFromFavorite_shouldThrow_whenUserIsNotAuthor() {
        Album album = Album.builder()
                .id(1L)
                .authorId(10L)
                .build();

        long userId = 99L;

        assertThrows(DataValidationException.class, () ->
                albumValidator.validateRemoveAlbumFromFavorite(album, userId));
    }

    @Test
    void validateUpdateAlbum_shouldThrow_whenUserIsNotAuthor() {
        Album album = Album.builder()
                .id(1L)
                .authorId(10L)
                .title("Old Title")
                .build();

        AlbumDto dto = new AlbumDto();
        dto.setTitle("New Title");
        dto.setDescription("desc");

        long userId = 99L;

        assertThrows(DataValidationException.class, () ->
                albumValidator.validateUpdateAlbum(album, userId, dto));
    }

    @Test
    void validateUpdateAlbum_shouldPass_whenTitleIsSame() {
        Album album = Album.builder()
                .id(1L)
                .authorId(10L)
                .title("Same Title")
                .build();

        AlbumDto dto = new AlbumDto();
        dto.setTitle("Same Title");
        dto.setDescription("desc");

        long userId = 10L;

        assertDoesNotThrow(() ->
                albumValidator.validateUpdateAlbum(album, userId, dto));
    }

    @Test
    void validateAlbumTitleIsUnique_shouldThrow_whenTitleAlreadyExists() {
        long userId = 1L;
        Album existingAlbum = Album.builder()
                .id(1L)
                .authorId(userId)
                .title("Existing Title")
                .build();

        Album newAlbum = Album.builder()
                .title("Existing Title")
                .build();

        when(albumRepository.findByAuthorId(userId))
                .thenReturn(Stream.of(existingAlbum));

        assertThrows(DataValidationException.class, () ->
                albumValidator.validateAlbumTitleIsUnique(userId, newAlbum));
    }

    @Test
    void validateAlbumTitleIsUnique_shouldPass_whenTitleIsUnique() {
        long userId = 1L;
        Album existingAlbum = Album.builder()
                .id(1L)
                .authorId(userId)
                .title("Old Title")
                .build();

        Album newAlbum = Album.builder()
                .title("New Unique Title")
                .build();

        when(albumRepository.findByAuthorId(userId))
                .thenReturn(Stream.of(existingAlbum));

        assertDoesNotThrow(() ->
                albumValidator.validateAlbumTitleIsUnique(userId, newAlbum));
    }

    @Test
    void validateDeleteAlbum_shouldThrow_whenUserIsNotAuthor() {
        Album album = Album.builder()
                .id(1L)
                .authorId(10L)
                .build();
        long userId = 99L;

        assertThrows(DataValidationException.class, () ->
                albumValidator.validateDeleteAlbum(album, userId));
    }

    @Test
    void validateDeleteAlbum_shouldPass_whenUserIsAuthor() {
        Album album = Album.builder()
                .id(1L)
                .authorId(10L)
                .build();
        long userId = 10L;

        assertDoesNotThrow(() ->
                albumValidator.validateDeleteAlbum(album, userId));
    }
}
