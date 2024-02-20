package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.album.AlbumValidator;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @InjectMocks
    private AlbumService albumService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private AlbumValidator albumValidator;
    @Spy
    private AlbumMapperImpl albumMapper = new AlbumMapperImpl();

    AlbumDto inputAlbumDto = new AlbumDto(null, "test", "test", 1l, null, null);

    AlbumDto albumDto = new AlbumDto(1l, "test", "test", 1l, null, null);

//    @Test
//    void shouldCreateAlbumService() {
//        List<Album> albumList = new ArrayList<>();
////        AlbumDto albumDto2 = new AlbumDto();
////        albumDto2.setId(0l);
////        albumDto2.setTitle(null);
////        albumDto2.setDescription(null);
////        albumDto2.setAuthorId(0l);
////        albumDto2.setCreatedAt(null);
////        albumDto2.setUpdatedAt(null);
//        Album album = new Album();
//        album.setAuthorId(1);
//        album.setTitle("test");
//        album.setDescription("test");
//
//        when(albumRepository.findByAuthorId(album.getAuthorId())).thenReturn(albumList.stream());
////        when(albumValidator.validate(albumList.stream(), albumDto)).thenReturn();
//        when(albumRepository.save(Mockito.any())).thenReturn(new Album());
//        when(albumMapper.toDto(Mockito.any())).thenReturn(new AlbumDto());
//        when(albumMapper.toEntity(Mockito.any())).thenReturn(new Album());
//
//        AlbumDto result = albumService.createAlbum(inputAlbumDto);
//        assertEquals(albumMapper.toDto(album), result);
//        assertAll(
//                () -> verify(albumRepository, times(1)).findByAuthorId(album.getAuthorId()),
//                () -> verify(albumRepository, times(1)).save(album),
//                () -> verify(albumMapper, times(1)).toDto(album),
//                () -> verify(albumMapper, times(1)).toEntity(albumDto),
//                () -> assertEquals(inputAlbumDto.getTitle(), result.getTitle())
//        );
//    }

//    @Test
//    void addPost() {
//        Post post = new Post();
//        post.setId(1);
//        Long albumId = 1l;
//        Album album = new Album();
//        album.addPost(post);
//        when(albumRepository.findById(albumId)).thenReturn(Optional.of(new Album()));
//        when(albumRepository.save(any())).thenReturn(any());
//        albumService.addPost(albumId, post);
//
//        verify(albumRepository, times(1)).findById(albumId);
//        verify(albumRepository, times(1)).save(new Album());
//    }

    @Test
    void removePost() {
    }

    @Test
    void shouldGetAlbumService() {
        long albumId = 1;
        Album album = new Album();
        album.setTitle("test");
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        AlbumDto result = albumService.getAlbum(albumId);
        assertNotNull(result);

    }

    @Test
    void updateAlbum() {
    }

    @Test
    void shouldDeleteAlbumService() {
        long albumId = 1;
        albumService.deleteAlbum(albumId);
        verify(albumRepository, times(1)).deleteById(albumId);
    }

    @Test
    void getAlbumsByFilter() {
    }

    @Test
    void filterHelp() {
    }

    @Test
    void albumFilter() {
    }

    @Test
    void addAlbumToFavorites() {
    }

    @Test
    void deleteAlbumFromFavorites() {
    }
}