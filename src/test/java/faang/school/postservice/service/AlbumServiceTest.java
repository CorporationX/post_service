package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AlbumService albumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAlbumsForUser_NoAlbums_ThrowsException() {
        Long userId = 1L;
        when(albumRepository.findAll()).thenReturn(Collections.emptyList());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            albumService.getAlbumsForUser(userId);
        });
        assertEquals("there is no albums for " + userId, exception.getMessage());
    }


    @Test
    public void testCanUserViewAlbum_InvalidCase_ThrowsException() throws Exception {
        Long userId = 1L;
        Album album = new Album();
        album.setVisibility(null);

        Method method = AlbumService.class.getDeclaredMethod("canUserViewAlbum", Long.class, Album.class);
        method.setAccessible(true);

        assertThrows(IllegalArgumentException.class, () -> {
            method.invoke(albumService, userId, album);
        });
    }

    @Test
    public void testCanUserViewAlbum_NullCase_ThrowsException() throws Exception {
        Long userId = 1L;
        Album album = null;

        Method method = AlbumService.class.getDeclaredMethod("canUserViewAlbum", Long.class, Album.class);
        method.setAccessible(true);

        assertThrows(NullPointerException.class, () -> {
            method.invoke(albumService, userId, album);
        });
    }

    @Test
    public void testCanUserViewAlbum_NullUserId_ThrowsException() throws Exception {
        Long userId = null;
        Album album = new Album();
        album.setVisibility(Album.Visibility.PUBLIC);

        Method method = AlbumService.class.getDeclaredMethod("canUserViewAlbum", Long.class, Album.class);
        method.setAccessible(true);

        assertThrows(NullPointerException.class, () -> {
            method.invoke(albumService, userId, album);
        });
    }
}