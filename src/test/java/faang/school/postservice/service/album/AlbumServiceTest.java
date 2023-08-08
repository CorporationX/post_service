package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private AlbumService albumService;

    @Test
    public void testDeleteAlbumOfCertainUser_Success() {
        long userId = 1L;
        long albumId = 1L;
        Album album = new Album();
        album.setAuthorId(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        DeleteResult result = albumService.deleteAlbumOfCertainUser(albumId);

        assertEquals(DeleteResult.DELETED, result);
        verify(albumRepository, times(1)).deleteById(albumId);
    }

    @Test
    public void testDeleteAlbumOfCertainUser_NotFound() {
        long albumId = 1L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        DeleteResult result = albumService.deleteAlbumOfCertainUser(albumId);

        assertEquals(DeleteResult.NOT_FOUND, result);
        verify(albumRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testDeleteAlbumOfCertainUser_NotAuthorized() {
        long userId = 1L;
        Album album = new Album();
        album.setAuthorId(2);

        when(userContext.getUserId()).thenReturn(userId);
        doReturn(Optional.of(album)).when(albumRepository).findById(2L);

        DeleteResult result = albumService.deleteAlbumOfCertainUser(2L);

        assertEquals(DeleteResult.NOT_AUTHORIZED, result);
        verify(albumRepository, never()).deleteById(anyLong());
    }
}
