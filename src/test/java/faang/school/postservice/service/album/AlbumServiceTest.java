package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumLightDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.AlbumController;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.UserVisibility;
import faang.school.postservice.model.VisibilityType;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.AlbumService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    @InjectMocks
    private AlbumService albumService;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private AlbumMapper albumMapper;

    private AlbumLightDto albumLightDto;
    private AlbumDto albumDto;
    private Album album;

    @BeforeEach
    void setUp() {
        albumLightDto = new AlbumLightDto(1L, "title", "desc");
        albumDto = new AlbumDto(1L, "title", "desc", 1L, List.of(1L),
                LocalDateTime.now(), LocalDateTime.now(), VisibilityType.All_USER, List.of(1L));
        Post post = new Post();
        post.setId(1L);
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        UserVisibility userVisibility = new UserVisibility();
        userVisibility.setId(1L);
        List<UserVisibility> userVisibilityList = new ArrayList<>();
        userVisibilityList.add(userVisibility);
        album = new Album(1L, "title", "desc", 1L, postList,
                LocalDateTime.of(2014, Month.APRIL, 8, 12, 30),
                LocalDateTime.of(2014, Month.MAY, 8, 12, 30),
                VisibilityType.All_USER, userVisibilityList);
    }

    @Test
    void testAlbumNull() {
        Long albumId = 1L;
        Long userId = 1L;
        Album album = new Album();
        album.setAuthorId(userId);
        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.of(album));
        when(albumMapper.toDto(album)).thenReturn(new AlbumDto());
//        when(albumService.toDto(album)).thenReturn(new AlbumDto());

        AlbumDto result = albumService.getAlbum(albumId);

        assertEquals(AlbumDto.class, result.getClass());
        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findByIdWithPosts(albumId);
        verify(albumMapper, times(1)).toDto(album);
    }

    @Test
    void createAlbum() {
        when(userContext.getUserId()).thenReturn(1L);
        when(albumMapper.toEntityLight(albumLightDto)).thenReturn(album);
        when(albumMapper.toDtoLight(albumRepository.save(any(Album.class)))).thenReturn(albumLightDto);
        when(albumRepository.existsByTitleAndAuthorId("title", 1L)).thenReturn(false);

        AlbumLightDto result = albumService.createAlbum(albumLightDto);

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).existsByTitleAndAuthorId(album.getTitle(), 1L);
        verify(albumMapper, times(1)).toEntityLight(albumLightDto);
        verify(albumMapper, times(1)).toDtoLight(any(Album.class));
        assertEquals(1, result.getId());
    }

    @Test
    void deleteAlbum() {
        when(albumRepository.findById(1L)).thenReturn(Optional.ofNullable(album));

        albumService.deleteAlbum(1L);
    }

    @Test
    public void testDeleteAlbumUnauthorizedUser() {
        when(albumRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                albumService.deleteAlbum(1L));

        assertEquals("album is not exist", exception.getMessage());
    }

    @Test
    void updateAlbum() {
        when(userContext.getUserId()).thenReturn(1L);
        when(albumMapper.toEntityLight(any(AlbumLightDto.class))).thenReturn(album);
        when(albumRepository.findById(1L)).thenReturn(Optional.ofNullable(album));
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        albumService.updateAlbum(albumLightDto);

        verify(albumMapper, times(1)).toEntityLight(albumLightDto);
        verify(albumRepository, times(1)).findById(anyLong());
    }

    @Test
    void addAlbumFavorite() {
        when(userContext.getUserId()).thenReturn(1L);

        albumService.addAlbumFavorite(1L);

        verify(albumRepository, times(1)).addAlbumToFavorites(1L, 1L);
    }

    @Test
    void deleteAlbumFavorite() {
        when(userContext.getUserId()).thenReturn(1L);

        albumService.deleteAlbumFavorite(1L);

        verify(albumRepository, times(1)).deleteAlbumFromFavorites(1L, 1L);
    }

    @Test
    void getAlbum() {
        when(userContext.getUserId()).thenReturn(1L);
        when(albumRepository.findByIdWithPosts(1L)).thenReturn(Optional.ofNullable(album));
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        albumService.getAlbum(1L);

        verify(albumMapper, times(1)).toDto(any(Album.class));
    }
}