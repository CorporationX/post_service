package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private AlbumMapperImpl albumMapper;
    @InjectMocks
    private AlbumService albumService;

    @Captor
    private ArgumentCaptor<Album> albumCaptor;

    private final Long userId = 1L;
    private final Long postId = 1L;
    private final Long albumId = 1L;
    AlbumDto albumDto = new AlbumDto();
    AlbumFilterDto filters = new AlbumFilterDto();
    Album album = new Album();
    Post post = new Post();


    @BeforeEach
    void setUp() {
        post.setId(postId);
        post.setContent("content");
        post.setAuthorId(userId);

        albumDto.setId(albumId);
        albumDto.setTitle("Title");
        albumDto.setDescription("description");
        albumDto.setAuthorId(userId);

        album.setId(albumId);
        album.setTitle("Title");
        album.setDescription("description");
        album.setAuthorId(userId);
        album.setPosts(new ArrayList<>(List.of(post)));
    }

    void setAlbumFilterDto() {
        filters.setTitle("Title");
        filters.setCreatedAt(LocalDateTime.now());
        filters.setCreatedBefore(true);
    }

    @Test
    void testCreateAlbum() {
        UserDto userDto = new UserDto(userId, "Name", "email");
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(userId)).thenReturn(List.of(new Album()));
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        AlbumDto result = albumService.createAlbum(userId, albumDto);

        verify(albumRepository, times(1)).save(albumCaptor.capture());
        Album album = albumCaptor.getValue();
        assertNotNull(result);
        assertEquals(albumDto.getTitle(), album.getTitle());
        assertEquals(albumDto.getDescription(), album.getDescription());
    }

    @Test
    void testAddPostToAlbum() {
        Post newPost = new Post();
        newPost.setId(2L);
        newPost.setContent("Test");
        when(postRepository.findById(newPost.getId())).thenReturn(Optional.of(newPost));
        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.of(album));
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        AlbumDto result = albumService.addPostToAlbum(userId, newPost.getId(), albumId);

        assertNotNull(result);
        assertEquals(albumDto.getTitle(), result.getTitle());
    }

    @Test
    void testDeletePostFromAlbumWithEmptyAlbum() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                albumService.deletePostFromAlbum(userId, postId, albumId));
    }

    @Test
    void testDeletePostFromAlbumWithEmptyPost() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                albumService.deletePostFromAlbum(userId, postId, albumId));
    }

    @Test
    void testGetAlbumById() {
        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.of(album));

        AlbumDto result = albumService.getAlbumById(albumId);

        assertNotNull(result);
        assertEquals(albumDto.getTitle(), result.getTitle());
    }

    @Test
    void testUpdateAlbumReturnUpdatedAlbumDto() {
        UserDto userDto = new UserDto(userId, "Name", "email");
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.findByIdWithPosts(albumDto.getId())).thenReturn(Optional.of(album));
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        AlbumDto result = albumService.updateAlbum(userId, albumDto);

        assertNotNull(result);
        assertEquals(albumDto.getTitle(), result.getTitle());
    }

    @Test
    void testGetAllAlbumsByAuthorIdWithFilters() {
        setAlbumFilterDto();
        album.setCreatedAt(LocalDateTime.now().minusDays(2));
        when(albumRepository.findByAuthorId(userId)).thenReturn(List.of(album));

        List<AlbumDto> result = albumService.getAllAlbumsByAuthorIdWithFilters(userId, filters);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
    }

    @Test
    void testGetAllAlbumsWithFilters() {
        setAlbumFilterDto();
        album.setCreatedAt(LocalDateTime.now().minusDays(2));
        when(albumRepository.findAllAlbums()).thenReturn(List.of(album));

        List<AlbumDto> result = albumService.getAllAlbumsWithFilters(filters);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
    }

    @Test
    void testAddFavouriteAlbum() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, "Name", "email"));

        albumService.addFavouriteAlbum(userId, albumId);

        verify(albumRepository, times(1)).addAlbumToFavorites(albumId, userId);
    }

    @Test
    void testAddFavouriteAlbumAndThrowException() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.addFavouriteAlbum(userId, albumId));
    }

    @Test
    void testDeleteFavouriteAlbum() {
        albumService.deleteFavouriteAlbum(userId, albumId);
        verify(albumRepository, times(1)).deleteAlbumFromFavorites(albumId, userId);
    }

    @Test
    void testGetFavouriteAlbumsByUserId() {
        setAlbumFilterDto();
        album.setCreatedAt(LocalDateTime.now().minusDays(2));
        when(albumRepository.findFavoriteAlbumsByUserId(userId)).thenReturn(List.of(album));

        List<AlbumDto> result = albumService.getFavouriteAlbumsByUserId(userId, filters);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
    }
}
