package faang.school.postservice.validator.album;

import faang.school.postservice.exception.album.AlbumException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ValidatorAlbumTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private AlbumValidator albumValidator;

    @Test
    public void addPostToAlbum_Non_ExistingAlbum_Test() {
        Mockito.when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        AlbumException albumException = Assertions.assertThrows(AlbumException.class,
                () -> albumValidator.addPostToAlbumValidateService(1, 1, 1));

        Assertions.assertEquals(albumException.getMessage(), "There is no album with such id");
    }

    @Test
    public void addPostToAlbum_Permission_Test() {
        Mockito.when(albumRepository.findById(1L)).thenReturn(Optional.ofNullable(Album.builder()
                        .authorId(1)
                .build()));

        AlbumException albumException = Assertions.assertThrows(AlbumException.class,
                () -> albumValidator.addPostToAlbumValidateService(2, 1, 1));

        Assertions.assertEquals(albumException.getMessage(), "You can add posts only in your albums");
    }

    @Test
    public void addPostToAlbum_PostNotFound_Test() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(albumRepository.findById(1L)).thenReturn(Optional.ofNullable(Album.builder()
                .authorId(1)
                .build()));

        AlbumException albumException = Assertions.assertThrows(AlbumException.class,
                () -> albumValidator.addPostToAlbumValidateService(1, 1, 1));

        Assertions.assertEquals(albumException.getMessage(), "There is no post with such id");
    }
}
