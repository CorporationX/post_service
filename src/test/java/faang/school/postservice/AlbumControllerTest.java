package faang.school.postservice;

import faang.school.postservice.controller.AlbumController;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumControllerTest {

    @Mock
    private AlbumService albumService;

    @InjectMocks
    private AlbumController albumController;

    private AlbumDto albumDto;

    @BeforeEach
    void setUp() {
        albumDto = new AlbumDto();
        albumDto.setTitle("Test Album");
        albumDto.setDescription("Test Description");
    }

    @Test
    public void testCreateAlbum() {
        when(albumService.create(any(AlbumDto.class))).thenReturn(albumDto);

        ResponseEntity<AlbumDto> response = albumController.create("1", albumDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Album", response.getBody().getTitle());
        assertEquals("Test Description", response.getBody().getDescription());
    }

    @Test
    public void testGetAlbumById() {
        when(albumService.getAlbumById(1L)).thenReturn(albumDto);

        ResponseEntity<AlbumDto> response = albumController.getAlbumById("1", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Album", response.getBody().getTitle());
        assertEquals("Test Description", response.getBody().getDescription());
    }

    @Test
    public void testAddAlbumToFavorites() {
        when(albumService.addAlbumToFavorites(1L)).thenReturn("Album with id = 1 added to favorites");

        ResponseEntity<String> response = albumController.addAlbumToFavorites("1", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Album with id = 1 added to favorites", response.getBody());
    }

    @Test
    public void testDeleteAlbumFromFavorites() {
        when(albumService.deleteAlbumFromFavorites(1L)).thenReturn("Album with id = 1 deleted from favorites");

        ResponseEntity<String> response = albumController.deleteAlbumFromFavorites("1", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Album with id = 1 deleted from favorites", response.getBody());
    }

    @Test
    public void testDeleteAlbum() {
        ResponseEntity<Void> response = albumController.delete("1", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetUserAlbums() {
        List<AlbumDto> albumList = Collections.singletonList(albumDto);
        when(albumService.getUserAlbums(any(AlbumFilterDto.class))).thenReturn(albumList);

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setTitle("Test");

        ResponseEntity<List<AlbumDto>> response = albumController.getUserAlbums("1", filterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Album", response.getBody().get(0).getTitle());
        assertEquals("Test Description", response.getBody().get(0).getDescription());
    }

    @Test
    public void testUpdateAlbum() {
        AlbumDto updatedDto = new AlbumDto();
        updatedDto.setTitle("Updated Title");
        updatedDto.setDescription("Updated Description");

        when(albumService.update(anyLong(), any(AlbumDto.class))).thenReturn(updatedDto);

        ResponseEntity<AlbumDto> response = albumController.update("1", 1L, updatedDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Title", response.getBody().getTitle());
        assertEquals("Updated Description", response.getBody().getDescription());
    }

    @Test
    public void testDeletePostFromAlbum() {
        when(albumService.deletePostFromAlbum(anyLong(), anyLong())).thenReturn(albumDto);

        ResponseEntity<AlbumDto> response = albumController.deletePostFromAlbum("1", 1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Album", response.getBody().getTitle());
        assertEquals("Test Description", response.getBody().getDescription());
    }
}
