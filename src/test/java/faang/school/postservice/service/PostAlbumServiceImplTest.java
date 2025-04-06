package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.PostAlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostAlbumMapper;
import faang.school.postservice.model.PostAlbum;
import faang.school.postservice.repository.PostAlbumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostAlbumServiceImplTest {

    @Mock
    private PostAlbumRepository postAlbumRepository;
    @Mock
    private PostAlbumMapper postAlbumMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private AlbumService albumService;

    @InjectMocks
    private PostAlbumIServiceImpl postAlbumService;

    @Test
    void testAddPostToAlbum_Success() {
        PostAlbumDto dto = new PostAlbumDto(1L, 1L, 1L, 1L);
        UserDto userDto = new UserDto(1L, "name", "email");
        PostAlbum postAlbum = new PostAlbum();

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumService.existsById(1L)).thenReturn(true);
        when(postAlbumMapper.toPostAlbum(dto)).thenReturn(postAlbum);
        when(postAlbumRepository.save(postAlbum)).thenReturn(postAlbum);
        when(postAlbumMapper.toPostAlbumDto(postAlbum)).thenReturn(dto);

        PostAlbumDto result = postAlbumService.addPostToAlbum(dto);

        assertEquals(dto, result);
    }

    @Test
    void testAddPostToAlbum_UserNotFound() {
        PostAlbumDto dto = new PostAlbumDto(1L, 1L, 1L, 1L);
        when(userServiceClient.getUser(1L)).thenReturn(null);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> postAlbumService.addPostToAlbum(dto));
        assertEquals("The user is not found", ex.getMessage());
    }

    @Test
    void testAddPostToAlbum_AlbumNotFound() {
        PostAlbumDto dto = new PostAlbumDto(1L, 1L, 1L, 1L);
        UserDto userDto = new UserDto(1L, "name", "email");

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumService.existsById(1L)).thenReturn(false);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> postAlbumService.addPostToAlbum(dto));
        assertEquals("The album is not found", ex.getMessage());
    }

    @Test
    void testAddPostToAlbum_UserNotOwner() {
        PostAlbumDto dto = new PostAlbumDto(1L, 1L, 2L, 1L);
        UserDto userDto = new UserDto(1L, "name", "email");

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumService.existsById(2L)).thenReturn(true);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> postAlbumService.addPostToAlbum(dto));
        assertEquals("The user is not the album owner", ex.getMessage());
    }
}
