package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.album.AlbumValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumMapper albumMapper;
    @Mock
    private AlbumValidator albumValidator;
    @Mock
    private PostService postService;
    @Mock
    private List<AlbumFilter> albumFilters;
    @InjectMocks
    private AlbumService albumService;

    @Test
    public void createAlbumTest() {
        var albumDto = AlbumDto.builder().build();
        Mockito.when(albumMapper.toEntity(albumDto)).thenReturn(new Album());
        albumService.createAlbum(albumDto, 1L);
        Mockito.verify(albumRepository, Mockito.times(1)).save(Mockito.any(Album.class));
    }

    @Test
    public void deleteAlbumTest() {
        var albumId = 1L;
        albumService.deleteAlbum(albumId, 1L);
        Mockito.verify(albumRepository, Mockito.times(1)).deleteById(albumId);
    }

    @Test
    public void addToFavoriteTest() {
        long albumId = 1L;
        albumService.addToFavorite(albumId, 1L);
        Mockito.verify(albumRepository, Mockito.times(1)).addAlbumToFavorites(albumId, 1L);
    }

    @Test
    public void removeFromFavoriteTest() {
        long albumId = 1L;
        albumService.removeFromFavorite(albumId, 1L);
        Mockito.verify(albumRepository, Mockito.times(1)).deleteAlbumFromFavorites(albumId, 1L);
    }

    @Test
    public void getAlbumTest() {
        var albumId = 1L;
        var album = Album.builder().visibility(Visibility.PUBLIC).build();
        Mockito.when(albumValidator.getAlbumFromDb(albumId)).thenReturn(album);
        albumService.getAlbum(albumId, 1L);
        Mockito.verify(albumValidator, Mockito.times(1)).getAlbumFromDb(albumId);
    }
}