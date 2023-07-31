package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.AlbumDataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class AlbumValidatorTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private AlbumValidator albumValidator;

    @Test
    public void validateAlbum_EmptyTitle_Test() {
        AlbumDto albumDto = AlbumDto.builder()
                .title("")
                .build();

        AlbumDataValidationException exception =
                Assertions.assertThrows(AlbumDataValidationException.class,
                        () -> albumValidator.validateAlbumController(albumDto));

        Assertions.assertEquals(exception.getMessage(), "Incorrect input data");
    }

    @Test
    public void validateAlbum_EmptyDescription_Test() {
        AlbumDto albumDto = AlbumDto.builder()
                .title("someTitle")
                .description("")
                .build();

        AlbumDataValidationException exception =
                Assertions.assertThrows(AlbumDataValidationException.class,
                        () -> albumValidator.validateAlbumController(albumDto));

        Assertions.assertEquals(exception.getMessage(), "Incorrect input data");
    }
    @Test
    public void validateAlbum_UserNotExist_Test() {
        AlbumDto albumDto = AlbumDto.builder()
                .authorId(1)
                .build();

        Mockito.when(userServiceClient.getUser(albumDto.getAuthorId())).thenReturn(null);

        AlbumDataValidationException exception =
                Assertions.assertThrows(AlbumDataValidationException.class,
                        () -> albumValidator.validateAlbumService(albumDto));
        Assertions.assertEquals(exception.getMessage(), "There is no user with such id");
    }

    @Test
    public void validateAlbum_UniqueTitle_Test() {
        AlbumDto albumDto = AlbumDto.builder()
                .authorId(1)
                .title("someTitle")
                .build();

        Mockito.when(userServiceClient.getUser(albumDto.getAuthorId())).thenReturn(new UserDto(1L, "Name", "email"));

        Stream<Album> albumStream = Stream.of(Album.builder()
                        .title("someTitle")
                .build());

        Mockito.when(albumRepository.findByAuthorId(1L)).thenReturn(albumStream);

        AlbumDataValidationException exception = Assertions.assertThrows(AlbumDataValidationException.class,
                () -> albumValidator.validateAlbumService(albumDto));
        Assertions.assertEquals(exception.getMessage(), "Title of the album should be unique");
    }
}
