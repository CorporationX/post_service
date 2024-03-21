package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.validator.album.AlbumValidator;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.album.AlbumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {
    @Mock
    private AlbumService albumService;
    @Mock
    private AlbumValidator albumValidator;
    @InjectMocks
    private AlbumController albumController;

    @Test
    void shouldCreateAlbumController() {
        AlbumDto albumDto = new AlbumDto();
        albumController.createAlbum(albumDto);
        verify(albumService, times(1)).createAlbum(albumDto);
        verify(albumValidator, times(1)).validate(albumDto.getTitle(), albumDto.getDescription(), albumDto.getAuthorId());
        verify(albumValidator, times(1)).validate(albumDto.getAuthorId());
    }

    @Test
    void shouldAddPostAlbumController() {
        Post post = new Post();
        long idAlbum = 0;
        albumController.addPost(idAlbum, post);
        verify(albumService, times(1)).addPost(idAlbum, post);
    }

    @Test
    void shouldRemovePostAlbumController() {
        long postId = 0;
        long albumId = 0;
        albumController.removePost(albumId, postId);
        verify(albumService, times(1)).removePost(albumId, postId);
    }

    @Test
    void shouldGetAlbumController() {
        long albumId = 0;
        albumController.getAlbum(albumId);
        verify(albumService, times(1)).getAlbum(albumId);
    }

    @Test
    void shouldGetByFilterAlbumController() {
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        albumController.getAlbumsByFilter(albumFilterDto);
        verify(albumService, times(1)).getAlbumsByFilter(albumFilterDto);
    }

    @Test
    void shouldUpdateAlbumController() {
        AlbumUpdateDto albumUpdateDto = new AlbumUpdateDto();
        long albumId = 1;
        albumController.updateAlbum(albumId, albumUpdateDto);
        verify(albumService, times(1)).updateAlbum(albumId, albumUpdateDto);
        verify(albumValidator, times(1)).validate(albumUpdateDto.getTitle(), albumUpdateDto.getDescription(), albumUpdateDto.getAuthorId());
        verify(albumValidator, times(1)).validate(albumUpdateDto.getAuthorId());
    }

    @Test
    void shouldDeleteAlbumController() {
        long albumId = 1;
        albumController.deleteAlbum(albumId);
        verify(albumService, times(1)).deleteAlbum(albumId);
    }

    @Test
    void shouldAddAlbumToFavoritesController() {
        long albumId = 1;
        long userId = 1;
        albumController.addAlbumToFavorites(albumId, userId);
        verify(albumService, times(1)).addAlbumToFavorites(albumId, userId);
        verify(albumValidator, times(1)).validate(userId);

    }

    @Test
    void deleteAlbumFromFavorites() {
        long albumId = 1;
        long userId = 1;
        albumController.deleteAlbumFromFavorites(albumId, userId);
        verify(albumService, times(1)).deleteAlbumFromFavorites(albumId, userId);
        verify(albumValidator, times(1)).validate(userId);
    }
}