package faang.school.postservice.exception.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.validator.album.AlbumValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AlbumValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AlbumValidator albumValidator;

    @Test
    void shouldValidateTitleAlbum() {
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,()->
                albumValidator.validate(null, null, null));
        assertEquals("Не заполнено название", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateDescriptionAlbum() {
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,()->
                albumValidator.validate("test", null, null));
        assertEquals("Не заполненл описание", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateAuthorAlbum() {

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,()->
                albumValidator.validate("test", "test", null));
        assertEquals("Не заполнен пользователь", dataValidationException.getMessage());
    }
    @Test
    void shouldValidateAlbumNameCreate() {
        Album album = new Album();
        album.setAuthorId(1l);
        album.setTitle("test");
        List<Album> albumList = new ArrayList<>();
        albumList.add(album);
        Stream<Album> albumStream = albumList.stream();
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAuthorId(1l);
        albumDto.setTitle("test");
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, ()->
                albumValidator.validate(albumStream, albumDto));
        assertEquals("Такое имя альбома уже есть", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateAlbumNameUpdate() {
        Album album = new Album();
        album.setAuthorId(1l);
        album.setTitle("test");
        List<Album> albumList = new ArrayList<>();
        albumList.add(album);
        Stream<Album> albumStream = albumList.stream();
        AlbumUpdateDto albumUpdateDto = new AlbumUpdateDto();
        albumUpdateDto.setAuthorId(1l);
        albumUpdateDto.setTitle("test");
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, ()->
                albumValidator.validate(albumStream, albumUpdateDto));
        assertEquals("Такое имя альбома уже есть", dataValidationException.getMessage());
    }


    @Test
    void shouldValidateAlbumlUser() {
        Long userId = 1l;
        userServiceClient.getUser(userId);
        verify(userServiceClient, timeout(1)).getUser(userId);
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            albumValidator.validate(userId);
        });
        assertEquals("Нет такого автора", dataValidationException.getMessage());
    }
}