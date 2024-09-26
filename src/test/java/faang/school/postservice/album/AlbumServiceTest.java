package faang.school.postservice.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.AlbumService;
import faang.school.postservice.service.filter.AlbumFilter;
import faang.school.postservice.service.filter.AlbumFilterByAfterTime;
import faang.school.postservice.service.filter.AlbumFilterByBeforeTime;
import faang.school.postservice.service.filter.AlbumFilterByTitlePattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Mock
    private List<AlbumFilter> albumFilters;

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
    public void testCreateAlbum_Success() {
        Album existingAlbum = Album.builder()
                .id(2L)
                .title("Kotlin")
                .build();

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(existingAlbum));
        when(albumRepository.save(album)).thenReturn(album);
        Album newAlbum = albumService.createAlbum(album);

        verify(albumRepository, times(1)).save(album);
        assertThat(newAlbum).usingRecursiveComparison().isEqualTo(album);
    }

    @Test
    public void testValidUserExist_Invalid() {
        when(userServiceClient.getUser(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> albumService.createAlbum(album));
    }

    @Test
    public void testValidUniqueAlbumTitleByAuthor_Invalid() {
        Album existingAlbum = Album.builder()
                .id(1L)
                .title("Java")
                .build();

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(1L)).thenReturn(Stream.of(existingAlbum));

        assertThrows(IllegalArgumentException.class, () -> albumService.createAlbum(album));
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
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    public void testValidAlbumBelongsToUser_Invalid() {
        Album album1 = Album.builder()
                .id(2L)
                .title("Kotlin")
                .authorId(2L)
                .build();

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album1));

        assertThrows(IllegalArgumentException.class, () -> albumService.addPostToAlbum(4L, albumId, authorId));
    }

    @Test
    public void testPostNotExist() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> albumService.addPostToAlbum(2L, albumId, authorId));
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
        verify(albumRepository, times(1)).save(album1);
    }

    @Test
    public void testAddAlbumToFavorite() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.existsById(albumId)).thenReturn(true);

        albumService.addAlbumToFavorite(albumId, authorId);

        verify(albumRepository, times(1)).addAlbumToFavorites(albumId, authorId);
    }

    @Test
    public void testRemoveAlbumFromFavorite() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findFavoriteAlbumsByUserId(authorId)).thenReturn(Stream.of(album));

        albumService.removeAlbumFromFavorite(albumId, authorId);

        verify(albumRepository, times(1)).deleteAlbumFromFavorites(albumId, authorId);
    }

    @Test
    public void testValidFavoriteContainsAlbum_Invalid() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findFavoriteAlbumsByUserId(authorId)).thenReturn(Stream.empty());

        assertThrows(IllegalArgumentException.class, () -> albumService.removeAlbumFromFavorite(albumId, authorId));
    }

    @Test
    public void testGetAlbum() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.getAlbum(albumId);

        verify(albumRepository, times(1)).findById(albumId);
    }

    @Test
    public void testGetAlbumFailed() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> albumService.getAlbum(albumId));
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

    @Test
    public void testDeleteAlbum() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.existsById(albumId)).thenReturn(true);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));

        albumService.deleteAlbum(albumId, authorId);
    }

    @Test
    public void testGetUserAlbumsByFilters() {
        Album albumOne = Album.builder()
                .id(10L)
                .authorId(authorId)
                .title("Java")
                .createdAt(LocalDateTime.of(2024, 9, 10, 0, 0))
                .build();
        Album albumTwo = Album.builder()
                .id(11L)
                .authorId(authorId)
                .title("Kotlin")
                .createdAt(LocalDateTime.of(2024, 5, 11, 0, 0))
                .build();
        Album albumThree = Album.builder()
                .id(12L)
                .authorId(authorId)
                .title("Java Core")
                .createdAt(LocalDateTime.of(2024, 7, 10, 0, 0))
                .build();

        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern("Java")
                .afterThisTime(LocalDateTime.of(2024, 6, 1, 0, 0))
                .beforeThisTime(LocalDateTime.of(2024, 8, 1, 0, 0))
                        .build();

        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(
                albumOne,
                albumTwo,
                albumThree));
        when(albumFilters.stream()).thenReturn(Stream.of(
                new AlbumFilterByAfterTime(),
                new AlbumFilterByBeforeTime(),
                new AlbumFilterByTitlePattern()));

        List<Album> filteredAlbums = albumService.getUserAlbumsByFilters(authorId, albumFilterDto);

        assertThat(filteredAlbums.get(0)).usingRecursiveComparison().isEqualTo(albumThree);
    }

    @Test
    public void testGetAlbumsByFilter() {
        Album albumOne = Album.builder()
                .id(10L)
                .authorId(10L)
                .title("Java")
                .createdAt(LocalDateTime.of(2024, 9, 10, 0, 0))
                .build();
        Album albumTwo = Album.builder()
                .id(11L)
                .authorId(13L)
                .title("Kotlin")
                .createdAt(LocalDateTime.of(2024, 5, 11, 0, 0))
                .build();
        Album albumThree = Album.builder()
                .id(12L)
                .authorId(15L)
                .title("Java Core")
                .createdAt(LocalDateTime.of(2024, 7, 10, 0, 0))
                .build();

        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern("Java")
                .afterThisTime(LocalDateTime.of(2024, 6, 1, 0, 0))
                .beforeThisTime(LocalDateTime.of(2024, 8, 1, 0, 0))
                .build();

        when(albumRepository.findAll()).thenReturn(List.of(
                albumOne,
                albumTwo,
                albumThree));
        when(albumFilters.stream()).thenReturn(Stream.of(
                new AlbumFilterByAfterTime(),
                new AlbumFilterByBeforeTime(),
                new AlbumFilterByTitlePattern()));

        List<Album> filteredAlbums = albumService.getAlbumsByFilter(albumFilterDto);

        assertThat(filteredAlbums.get(0)).usingRecursiveComparison().isEqualTo(albumThree);
    }

    @Test
    public void testFavoriteUserAlbumsByFilters() {
        Album albumOne = Album.builder()
                .id(10L)
                .authorId(authorId)
                .title("Java")
                .createdAt(LocalDateTime.of(2024, 9, 10, 0, 0))
                .build();
        Album albumTwo = Album.builder()
                .id(11L)
                .authorId(authorId)
                .title("Kotlin")
                .createdAt(LocalDateTime.of(2024, 5, 11, 0, 0))
                .build();
        Album albumThree = Album.builder()
                .id(12L)
                .authorId(authorId)
                .title("Java Core")
                .createdAt(LocalDateTime.of(2024, 7, 10, 0, 0))
                .build();

        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern("Java")
                .afterThisTime(LocalDateTime.of(2024, 6, 1, 0, 0))
                .beforeThisTime(LocalDateTime.of(2024, 8, 1, 0, 0))
                .build();

        when(albumRepository.findFavoriteAlbumsByUserId(authorId)).thenReturn(Stream.of(
                albumOne,
                albumTwo,
                albumThree));
        when(albumFilters.stream()).thenReturn(Stream.of(
                new AlbumFilterByAfterTime(),
                new AlbumFilterByBeforeTime(),
                new AlbumFilterByTitlePattern()));

        List<Album> filteredAlbums = albumService.getFavoriteUserAlbumsByFilters(authorId, albumFilterDto);

        assertThat(filteredAlbums.get(0)).usingRecursiveComparison().isEqualTo(albumThree);
    }
}
