package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserAlbumAccessRepository;
import faang.school.postservice.service.AlbumService;
import faang.school.postservice.service.filter.AlbumFilter;
import faang.school.postservice.service.filter.AlbumFilterByAfterTime;
import faang.school.postservice.service.filter.AlbumFilterByBeforeTime;
import faang.school.postservice.service.filter.AlbumFilterByTitlePattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    private AlbumRepository albumRepository = mock(AlbumRepository.class);
    private UserServiceClient userServiceClient = mock(UserServiceClient.class);
    private PostRepository postRepository = mock(PostRepository.class);
    private UserAlbumAccessRepository userAlbumAccessRepository = mock(UserAlbumAccessRepository.class);
    private List<AlbumFilter> albumFilters = mock(List.class);
    private AlbumService albumService = new AlbumService(albumRepository, userServiceClient,
            postRepository, userAlbumAccessRepository, albumFilters);

    private UserDto userDto;
    private long authorId;
    private long albumId;
    private Album album;
    private Album otherAlbum;
    private Album albumWithPost;
    private long userId;
    private long postId;
    private Post post;
    private Album albumOne;
    private Album albumTwo;
    private Album albumThree;
    private Album albumFour;
    private Album albumFive;
    private AlbumFilterDto albumFilterDto;


    @BeforeEach
    public void setUp() {

        userDto = UserDto.builder()
                .id(112L)
                .username("Frank")
                .build();

        authorId = 1L;
        albumId = 1L;
        userId = 10L;

        postId = 1L;
        post = Post.builder()
                .id(postId)
                .build();

        album = Album.builder()
                .id(albumId)
                .authorId(authorId)
                .title("Java")
                .posts(new ArrayList<>())
                .build();

        albumWithPost = Album.builder()
                .id(albumId)
                .authorId(authorId)
                .title("Java")
                .posts(new ArrayList<>(List.of(post)))
                .build();

        otherAlbum = Album.builder()
                .id(2L)
                .authorId(2L)
                .title("Kotlin")
                .posts(new ArrayList<>())
                .build();

        albumOne = Album.builder()
                .id(10L)
                .authorId(authorId)
                .title("Kotlin")
                .createdAt(LocalDateTime.of(2024, 7, 10, 0, 0))
                .status(AlbumVisibility.ONLY_AUTHOR)
                .build();
        albumTwo = Album.builder()
                .id(11L)
                .authorId(authorId)
                .title("Java")
                .createdAt(LocalDateTime.of(2024, 9, 11, 0, 0))
                .status(AlbumVisibility.ONLY_AUTHOR)
                .build();
        albumThree = Album.builder()
                .id(12L)
                .authorId(authorId)
                .title("Java Core")
                .createdAt(LocalDateTime.of(2024, 5, 10, 0, 0))
                .status(AlbumVisibility.ONLY_AUTHOR)
                .build();
        albumFour = Album.builder()
                .id(13L)
                .authorId(authorId)
                .title("Java Framework")
                .createdAt(LocalDateTime.of(2024, 7, 20, 0, 0))
                .status(AlbumVisibility.ONLY_AUTHOR)
                .build();
        albumFive = Album.builder()
                .id(13L)
                .authorId(authorId)
                .title("Java Collection")
                .createdAt(LocalDateTime.of(2024, 7, 30, 0, 0))
                .status(AlbumVisibility.ALL)
                .build();

        albumFilterDto = AlbumFilterDto.builder()
                .titlePattern("Java")
                .afterThisTime(LocalDateTime.of(2024, 6, 1, 0, 0))
                .beforeThisTime(LocalDateTime.of(2024, 8, 1, 0, 0))
                .build();
    }

    @Test
    @DisplayName("testCreateAlbum_Success")
    public void testCreateAlbum_Success() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of());
        when(albumRepository.save(album)).thenReturn(album);
        Album newAlbum = albumService.createAlbum(album);

        verify(albumRepository, times(1)).save(album);
        assertThat(newAlbum).usingRecursiveComparison().isEqualTo(album);
    }

    @Test
    @DisplayName("testValidUserExist_Invalid")
    public void testValidUserExist_Invalid() {
        when(userServiceClient.getUser(authorId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> albumService.createAlbum(album));
    }

    @Test
    @DisplayName("testValidUniqueAlbumTitleByAuthor_Invalid")
    public void testValidUniqueAlbumTitleByAuthor_Invalid() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));

        assertThrows(IllegalArgumentException.class, () -> albumService.createAlbum(album));
    }

    @Test
    @DisplayName("testAddPostToAlbum_Success")
    public void testAddPostToAlbum_Success() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(albumRepository.save(album)).thenReturn(album);

        albumService.addPostToAlbum(postId, albumId, authorId);

        assertThat(album.getPosts().get(0)).usingRecursiveComparison().isEqualTo(post);
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    @DisplayName("testValidAlbumBelongsToUser_Invalid")
    public void testValidAlbumBelongsToUser_Invalid() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(userId)).thenReturn(Stream.of(otherAlbum));

        assertThrows(IllegalArgumentException.class, () -> albumService.addPostToAlbum(postId, albumId, userId));
    }

    @Test
    @DisplayName("testPostNotExist")
    public void testPostNotExist() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> albumService.addPostToAlbum(2L, albumId, authorId));
    }

    @Test
    @DisplayName("testRemovePostFromAlbum")
    public void testRemovePostFromAlbum() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(albumWithPost));
        when(albumRepository.save(album)).thenReturn(albumWithPost);

        albumService.removePostFromAlbum(postId, albumId, authorId);

        assertTrue(albumWithPost.getPosts().isEmpty());
        verify(albumRepository, times(1)).save(albumWithPost);
    }

    @Test
    @DisplayName("testAddAlbumToFavorite")
    public void testAddAlbumToFavorite() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(albumRepository.existsById(albumId)).thenReturn(true);

        albumService.addAlbumToFavorite(albumId, authorId);

        verify(albumRepository, times(1)).addAlbumToFavorites(albumId, authorId);
    }

    @Test
    @DisplayName("testRemoveAlbumFromFavorite")
    public void testRemoveAlbumFromFavorite() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(albumRepository.findFavoriteAlbumsByUserId(authorId)).thenReturn(Stream.of(album));

        albumService.removeAlbumFromFavorite(albumId, authorId);

        verify(albumRepository, times(1)).deleteAlbumFromFavorites(albumId, authorId);
    }

    @Test
    @DisplayName("testValidFavoriteContainsAlbum_Invalid")
    public void testValidFavoriteContainsAlbum_Invalid() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(albumRepository.findFavoriteAlbumsByUserId(authorId)).thenReturn(Stream.empty());

        assertThrows(IllegalArgumentException.class, () -> albumService.removeAlbumFromFavorite(albumId, authorId));
    }

    @Test
    @DisplayName("testGetAlbumsWithALLStatus_Success()")
    public void testGetAlbumsWithALLStatus_Success() {
        album.setStatus(AlbumVisibility.ALL);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));


        Album newAlbum = albumService.getAlbum(albumId, userId);
        assertThat(newAlbum).usingRecursiveComparison().isEqualTo(album);
    }

    @Test
    @DisplayName("testGetAlbumsWithALLStatus_Success")
    public void testGetAlbumsWithSubscriberStatus_Success() {
        UserDto userWithFollowings = UserDto.builder()
                .followingsIds(List.of(userId))
                .build();

        album.setStatus(AlbumVisibility.SUBSCRIBERS);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(album.getAuthorId())).thenReturn(userWithFollowings);

        Album newAlbum = albumService.getAlbum(albumId, userId);
        assertThat(newAlbum).usingRecursiveComparison().isEqualTo(album);
    }

    @Test
    @DisplayName("testGetAlbumsWithALLStatus_Invalid")
    public void testGetAlbumsWithSubscriberStatus_Invalid() {
        UserDto userWithFollowings = UserDto.builder()
                .followingsIds(List.of())
                .build();

        album.setStatus(AlbumVisibility.SUBSCRIBERS);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(album.getAuthorId())).thenReturn(userWithFollowings);

        assertThrows(IllegalArgumentException.class,
                () -> albumService.getAlbum(albumId, userId));
    }

    @Test
    @DisplayName("testGetAlbumsWithAllowedUsersStatus_Success")
    public void testGetAlbumsWithAllowedUsersStatus_Success() {
        album.setStatus(AlbumVisibility.ALLOWED_USERS);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userAlbumAccessRepository.hasUserAccessToAlbum(userId, albumId)).thenReturn(true);

        Album newAlbum = albumService.getAlbum(albumId, userId);
        assertThat(newAlbum).usingRecursiveComparison().isEqualTo(album);
    }

    @Test
    @DisplayName("testGetAlbumsWithAllowedUsersStatus_Invalid")
    public void testGetAlbumsWithAllowedUsersStatus_Invalid() {
        album.setStatus(AlbumVisibility.ALLOWED_USERS);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userAlbumAccessRepository.hasUserAccessToAlbum(userId, albumId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> albumService.getAlbum(albumId, userId));
    }

    @Test
    @DisplayName("testGetAlbumsWithOnlyAuthor_Success")
    public void testGetAlbumsWithOnlyAuthor_Success() {
        album.setStatus(AlbumVisibility.ONLY_AUTHOR);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        Album newAlbum = albumService.getAlbum(albumId, authorId);
        assertThat(newAlbum).usingRecursiveComparison().isEqualTo(album);
    }

    @Test
    @DisplayName("testGetAlbumsWithOnlyAuthor_Invalid")
    public void testGetAlbumsWithOnlyAuthor_Invalid() {
        album.setStatus(AlbumVisibility.ONLY_AUTHOR);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        assertThrows(IllegalArgumentException.class,
                () -> albumService.getAlbum(albumId, userId));
    }

    @Test
    @DisplayName("testGetAlbumFailed")
    public void testGetAlbumFailed() {
        when(albumRepository.findById(albumId))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> albumService.getAlbum(albumId, userId));
    }

    @Test
    @DisplayName("testUpdateAlbum")
    public void testUpdateAlbum() {
        String newTitle = "Swift";
        String newDescription = "For IOS";
        Album newAlbum = Album.builder()
                .id(album.getId())
                .authorId(album.getAuthorId())
                .title(newTitle)
                .description(newDescription)
                .status(AlbumVisibility.ALLOWED_USERS)
                .posts(List.of())
                .build();

        album.setStatus(AlbumVisibility.ALLOWED_USERS);

        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.updateAlbum(albumId, authorId, newAlbum);

        assertThat(album).usingRecursiveComparison().isEqualTo(newAlbum);
    }

    @Test
    @DisplayName("testDeleteAlbum")
    public void testDeleteAlbum() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(albumRepository.existsById(albumId)).thenReturn(true);
        when(albumRepository.findByAuthorId(authorId)).thenReturn(Stream.of(album));

        albumService.deleteAlbum(albumId, authorId);
    }

    @Captor
    ArgumentCaptor<List<Long>> captor;

    @Test
    @DisplayName("testGetAllAvailableAlbums_Success")
    public void testGetAllAvailableAlbums_Success() {
        UserDto userDto = UserDto.builder()
                .id(userId)
                .followingsIds(new ArrayList<>())
                .build();

        when(albumRepository.findAlbumIdsAllStatus()).thenReturn(List.of(1L));
        when(albumRepository.findAlbumsIdsOnlyAuthor(userId)).thenReturn(List.of(2L));
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.findAlbumIdsByAuthorIdsAndSubsStatus(List.of(userId))).thenReturn(List.of(3L));
        when(userAlbumAccessRepository.findAlbumIdsAllowedUser(userId)).thenReturn(List.of(4L, 5L));

        albumService.getAllAvailableAlbums(userId);

        verify(albumRepository).findAllById(captor.capture());

        assertIterableEquals(List.of(1L, 2L, 3L, 4L, 5L), captor.getValue());
    }




    @Test
    @DisplayName("testGetAllAvailableAlbumsByFilters")
    public void testGetAllAvailableAlbumsByFilters() {
        UserDto userDto = UserDto.builder()
                .id(userId)
                .followingsIds(new ArrayList<>())
                .build();

        when(albumRepository.findAlbumIdsAllStatus()).thenReturn(List.of(1L));
        when(albumRepository.findAlbumsIdsOnlyAuthor(userId)).thenReturn(List.of(2L));
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.findAlbumIdsByAuthorIdsAndSubsStatus(List.of(userId))).thenReturn(List.of(3L));
        when(userAlbumAccessRepository.findAlbumIdsAllowedUser(userId)).thenReturn(List.of(4L, 5L));

        when(albumRepository.findAllById(List.of(1L, 2L, 3L, 4L, 5L))).thenReturn(List.of(
                albumOne,
                albumTwo,
                albumThree,
                albumFour,
                albumFive));

        when(albumFilters.stream()).thenReturn(Stream.of(
                new AlbumFilterByAfterTime(),
                new AlbumFilterByBeforeTime(),
                new AlbumFilterByTitlePattern()));

        List<Album> filteredAlbums = albumService.getAllAvailableAlbumsByFilters(userId, albumFilterDto);
        assertThat(filteredAlbums.get(0)).usingRecursiveComparison().isEqualTo(albumFour);
    }
}