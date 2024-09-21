package faang.school.postservice.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.AlbumService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    @InjectMocks
    private AlbumService albumService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostRepository postRepository;


//    @Spy
//    private AlbumMapper albumMapper = Mappers.getMapper(AlbumMapper.class);

    private Album album;
    private long albumId = 1L;
    private long authorId = 1L;

    private UserDto userDto = new UserDto(1L, "Denis", "");


    @BeforeEach
    public void setUp() {
        album = Album.builder()
                .id(albumId)
                .authorId(authorId)
                .title("Java")
                .posts(new ArrayList<>())
                .build();
    }

    @Test
    public void testCreateAlbumSuccess() {
        Album existingAlbum = Album.builder()
                .id(2L)
                .title("Kotlin")
                .build();
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(existingAlbum));

        albumService.createAlbum(album);

        verify(albumRepository).save(album);
    }

    @Test
    public void testCreateAlbum_userNotFound() {
        when(userServiceClient.getUser(1L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> albumService.createAlbum(album));
        assertEquals("This user does not exist.", exception.getMessage());
    }

    @Test
    public void testCreateExistingAlbum() {
        Album existingAlbum = Album.builder()
                .id(1L)
                .title("Java")
                .build();

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(1L)).thenReturn(Stream.of(existingAlbum));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> albumService.createAlbum(album));
        assertEquals("The album name must be unique for this user.", exception.getMessage());
    }

    @Test
    public void testAddPostToAlbum_Success() {
        long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .build();
        Album album1 = Album.builder()
                .id(album.getId())
                .title(album.getTitle())
                .authorId(album.getAuthorId())
                .posts(new ArrayList<>(List.of(post)))
                .build();

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(albumRepository.save(album)).thenReturn(album1);

        albumService.addPostToAlbum(postId, albumId, authorId);
        assertThat(album.getPosts().get(0)).usingRecursiveComparison().isEqualTo(post);
        verify(albumRepository, atMostOnce()).save(album);
    }

    @Test
    public void testAddPostAlbum_Fail() {
        Album album1 = Album.builder()
                .id(2L)
                .title("Kotlin")
                .authorId(2L)
                .build();
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album1));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> albumService.addPostToAlbum(4L, albumId, authorId));
        assertEquals("This album does not belong to the user.", exception.getMessage());
    }

    @Test
    public void testPostMissing() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> albumService.addPostToAlbum(2L, albumId, authorId));
    }

    @Test
    public void testRemovePostFromAlbum() {
        long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .build();
        Album album1 = Album.builder()
                .id(album.getId())
                .title(album.getTitle())
                .authorId(album.getAuthorId())
                .posts(new ArrayList<>(List.of(post)))
                .build();

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album1));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album1));
        when(albumRepository.save(album)).thenReturn(album1);

        albumService.removePostFromAlbum(postId, albumId, authorId);

        assertTrue(album1.getPosts().isEmpty());
        verify(albumRepository, atMostOnce()).save(album1);
    }

    @Test
    public void testAddAlbumToFavorite() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.existsById(albumId)).thenReturn(true);

        albumService.addAlbumToFavorite(albumId, authorId);

        verify(albumRepository, atMostOnce()).addAlbumToFavorites(albumId, authorId);
    }

    @Test
    public void testRemoveAlbumFromFavorite() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findFavoriteAlbumsByUserId(authorId)).thenReturn(Stream.of(album));

        albumService.removeAlbumFromFavorite(albumId, authorId);

        verify(albumRepository, atMostOnce()).deleteAlbumFromFavorites(albumId, authorId);
    }

    @Test
    public void testRemoveAlbumFrom_NotFavorite() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findFavoriteAlbumsByUserId(authorId)).thenReturn(Stream.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> albumService.removeAlbumFromFavorite(albumId, authorId));
        assertEquals("This album is not in the favorites list for this user", exception.getMessage());
    }

    @Test
    public void testGetAlbum() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.getAlbum(albumId);
        verify(albumRepository, atMostOnce()).findById(albumId);
    }

    @Test
    public void testGetAlbumFailed() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> albumService.getAlbum(albumId));
    }

    @Test
    public void testUpdateAlbum() {
        String newTitle = "Swift";
        String newDescription = "For IOS";
        Album newAlbum = Album.builder()
                .id(album.getId())
                .authorId(album.getAuthorId())
                .title(newTitle)
                .description(newDescription)
                .posts(List.of())
                .build();

        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.updateAlbum(albumId, authorId, newAlbum);
        assertThat(album).usingRecursiveComparison().isEqualTo(newAlbum);
    }

}
