package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClientMock;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumChosenUsers;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumAuthorFilter;
import faang.school.postservice.service.album.filter.AlbumCreatedFromFilter;
import faang.school.postservice.service.album.filter.AlbumCreatedToFilter;
import faang.school.postservice.service.album.filter.AlbumFilter;
import faang.school.postservice.service.album.filter.AlbumTitleFilter;
import faang.school.postservice.service.album.filter.MinimumOfPostsAtAlbum;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static faang.school.postservice.model.AlbumVisibility.ALL_USERS;
import static faang.school.postservice.model.AlbumVisibility.AUTHOR_ONLY;
import static faang.school.postservice.model.AlbumVisibility.CHOSEN_USERS;
import static faang.school.postservice.model.AlbumVisibility.SUBSCRIBERS_ONLY;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.ALREADY_FAVORITE;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.NOT_FAVORITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private AlbumServiceChecker checker;
    @Mock
    private UserServiceClientMock userServiceClient;
    @Mock
    private List<AlbumFilter> albumFilters;

    @InjectMocks
    private AlbumService albumService;

    private final List<AlbumFilter> filters = List.of(
        new AlbumAuthorFilter(),
        new AlbumTitleFilter(),
        new MinimumOfPostsAtAlbum(),
        new AlbumCreatedFromFilter(),
        new AlbumCreatedToFilter()
    );
    private final Random random = new Random();

    @Test
    void createNewAlbum() {
        long authorId = 1;
        Album album = Album.builder()
            .title("Title")
            .description("Description")
            .build();
        when(albumRepository.save(Mockito.any(Album.class))).thenReturn(album);

        albumService.createAlbum(authorId, album, null);

        verify(checker, Mockito.times(1)).checkUserExists(authorId);
        verify(checker, Mockito.times(1)).checkAlbumExistsWithTitle(album.getTitle(), authorId);
        verify(albumRepository, Mockito.times(1)).save(album);
    }

    @Test
    void getAlbum() {
        long userId = 1;
        long albumId = 2;
        Album expected = new Album();
        when(checker.findByIdWithPosts(albumId)).thenReturn(expected);
        Album album = albumService.getAlbum(userId, albumId);

        verify(checker, Mockito.times(1)).checkUserExists(userId);
        verify(checker, Mockito.times(1)).findByIdWithPosts(albumId);
        assertEquals(expected, album);
    }

    @Test
    void testUpdateAlbum() {
        long userId = 1;
        String title = "New title";
        String description = "New description";

        Album album = forUpdateAlbumTest(userId, title, description);

        verify(checker, Mockito.times(1)).checkAlbumExistsWithTitle(title, userId);
        assertEquals(album.getTitle(), title);
        assertEquals(album.getDescription(), description);
    }

    @Test
    void testUpdateAlbumWhenDescriptionIsNull() {
        long userId = 1;
        String title = "New title";
        String description = null;

        Album album = forUpdateAlbumTest(userId, title, description);

        verify(checker, Mockito.times(1)).checkAlbumExistsWithTitle(title, userId);
        assertEquals(album.getTitle(), title);
        assertNotNull(album.getDescription());
    }

    @Test
    void testUpdateAlbumWhenEmptyTitle() {
        long userId = 1;
        String title = " ";
        String description = "New description";

        Album album = forUpdateAlbumTest(userId, title, description);

        assertNotEquals(album.getTitle(), title);
        assertEquals(album.getDescription(), description);
    }

    @Test
    void testDeleteAlbum() {
        long userId = 1;
        long albumId = 2;
        Album album = Album.builder()
            .id(2)
            .authorId(1)
            .build();
        when(checker.findByIdWithPosts(albumId)).thenReturn(album);

        albumService.deleteAlbum(userId, albumId);

        verify(checker, Mockito.times(1)).checkUserExists(userId);
        verify(checker, Mockito.times(1)).findByIdWithPosts(albumId);
        verify(albumRepository, Mockito.times(1)).delete(album);
    }

    @Test
    void addAlbumToFavorites() {
        long userId = 1;
        long albumId = 2;
        boolean isContains = true;
        when(checker.findByIdWithPosts(albumId)).thenReturn(new Album());

        Album album = albumService.addAlbumToFavorites(userId, albumId);

        verify(checker, Mockito.times(1)).checkUserExists(userId);
        verify(checker, Mockito.times(1))
            .checkFavoritesAlbumsContainsAlbum(userId, album, ALREADY_FAVORITE, isContains);
        verify(albumRepository, Mockito.times(1)).addAlbumToFavorites(albumId, userId);
    }

    @Test
    void deleteAlbumFromFavorites() {
        long userId = 1;
        long albumId = 2;
        boolean isContains = false;
        when(checker.findByIdWithPosts(albumId)).thenReturn(new Album());

        Album album = albumService.deleteAlbumFromFavorites(userId, albumId);

        verify(checker, Mockito.times(1)).checkUserExists(userId);
        verify(checker, Mockito.times(1))
            .checkFavoritesAlbumsContainsAlbum(userId, album, NOT_FAVORITE, isContains);
        verify(albumRepository, Mockito.times(1)).deleteAlbumFromFavorites(albumId, userId);
    }

    @Test
    void addNewPosts() {
        long userId = 1;
        long albumId = 2;
        List<Post> posts = new ArrayList<>(List.of(new Post()));
        Album album = Album.builder()
            .id(albumId)
            .title("Title")
            .authorId(userId)
            .posts(posts)
            .build();
        List<Long> postsIds = List.of(1L, 4L);
        Post post1 = new Post();
        post1.setId(postsIds.get(0));
        Post post2 = new Post();
        post2.setId(postsIds.get(1));
        List<Post> postToAdd = List.of(post1, post2);
        when(checker.findByIdWithPosts(albumId)).thenReturn(album);
        when(checker.isExistingPosts(1)).thenReturn(true);
        when(checker.isExistingPosts(4)).thenReturn(true);
        when(postRepository.findAllById(postsIds)).thenReturn(postToAdd);

        albumService.addNewPosts(userId, albumId, postsIds);

        assertEquals(album.getPosts().size(), 3);
        assertTrue(album.getPosts().contains(post1));
        assertTrue(album.getPosts().contains(post2));
        verify(checker, Mockito.times(1)).checkUserExists(userId);
        verify(checker, Mockito.times(1)).findByIdWithPosts(albumId);
        verify(albumRepository, Mockito.times(1)).save(album);
    }

    @Test
    void deletePosts() {
        long userId = 1;
        long albumId = 2;
        Post post1 = new Post();
        post1.setId(3L);
        List<Post> expected = List.of(post1);
        List<Post> posts = LongStream.rangeClosed(1, 5)
            .mapToObj(id -> {
                Post post = new Post();
                post.setId(id);
                return post;
            })
            .collect(Collectors.toCollection(ArrayList::new));
        List<Long> postIdsToDelete = List.of(4L, 2L, 5L, 1L);
        Album album = Album.builder()
            .id(albumId)
            .title("Title")
            .authorId(userId)
            .posts(posts)
            .build();
        when(checker.findByIdWithPosts(albumId)).thenReturn(album);

        albumService.deletePosts(userId, albumId, postIdsToDelete);

        assertEquals(album.getPosts().size(), 1);
        assertEquals(album.getPosts(), expected);
        verify(checker, Mockito.times(1)).checkUserExists(userId);
        verify(checker, Mockito.times(1)).findByIdWithPosts(albumId);
        verify(albumRepository, Mockito.times(1)).save(album);
    }

    @Test
    void getUserAlbums() {
        long userId = 1;
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
            .title("Some title")
            .createdFrom(LocalDateTime.now().minusMonths(1))
            .build();
        List<Album> albums = createAlbumsStreamWithTitlesAndCreatedAt(1, 10,
            1, 20, "Some title")
            .collect(Collectors.toCollection(ArrayList::new));
        List<Album> expected = new ArrayList<>(albums);
        createAlbumsStreamWithTitlesAndCreatedAt(11, 20,
            32, 50, "Some title")
            .forEach(albums::add);
        createAlbumsStreamWithTitlesAndCreatedAt(21, 30,
            1, 25, "Some else title")
            .forEach(albums::add);
        when(albumFilters.stream()).thenReturn(filters.stream());
        when(albumRepository.findByAuthorId(userId)).thenReturn(albums);

        List<Album> result = albumService.getUserAlbums(userId, albumFilterDto);

        assertEquals(expected, result);
        verify(checker, Mockito.times(1)).checkUserExists(userId);
    }

    @Test
    void getFavoriteAlbums() {
        long userId = 1;
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
            .minQuantityOfPosts(10)
            .build();
        List<Album> albums = createAlbumsStreamWithPosts(1, 10, 11, 30)
            .collect(Collectors.toCollection(ArrayList::new));
        List<Album> expected = new ArrayList<>(albums);
        createAlbumsStreamWithPosts(11, 20, 0, 9)
            .forEach(albums::add);
        when(albumFilters.stream()).thenReturn(filters.stream());
        when(albumRepository.findFavoriteAlbumsByUserId(userId)).thenReturn(albums.stream());

        List<Album> result = albumService.getFavoriteAlbums(userId, albumFilterDto);

        verify(checker, Mockito.times(1)).checkUserExists(userId);
        assertEquals(expected, result);
    }

    @Test
    void getAllAlbums() {
        long userId = 1;
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
            .authorId(1L)
            .createdTo(LocalDateTime.now().minusMonths(1))
            .build();
        List<Album> albums = createAlbumsStreamWithAlbumsIdAndCreatedAt(1, 10,
            32, 50, 1)
            .collect(Collectors.toCollection(ArrayList::new));
        List<Album> expected = new ArrayList<>(albums);
        createAlbumsStreamWithAlbumsIdAndCreatedAt(11, 20,
            32, 50, 2)
            .forEach(albums::add);
        createAlbumsStreamWithAlbumsIdAndCreatedAt(21, 30,
            1, 25, 2)
            .forEach(albums::add);
        when(albumFilters.stream()).thenReturn(filters.stream());
        when(albumRepository.findAll()).thenReturn(albums);

        List<Album> result = albumService.getAllAlbums(userId, albumFilterDto);

        verify(checker, Mockito.times(1)).checkUserExists(userId);
        assertEquals(expected, result);
    }

    private Stream<Album> createAlbumsStreamWithPosts(long fromId, long toId,
                                                      long fromQuantityOfPosts, long toQuantityOfPosts) {
        List<Post> posts = LongStream.rangeClosed(1, getRandomLong(fromQuantityOfPosts, toQuantityOfPosts))
            .mapToObj(id -> Post.builder()
                .id(id)
                .build())
            .toList();
        return LongStream.rangeClosed(fromId, toId)
            .mapToObj(id -> Album.builder()
                .id(id)
                .authorId(random.nextLong(1, 25))
                .posts(posts)
                .build());
    }

    private Stream<Album> createAlbumsStreamWithAlbumsIdAndCreatedAt(long fromId, long toId,
                                                                     long fromMinusDays, long toMinusDays,
                                                                     long authorId) {
        return LongStream.rangeClosed(fromId, toId)
            .mapToObj(id -> Album.builder()
                .id(id)
                .authorId(authorId)
                .createdAt(LocalDateTime.now().minusDays(getRandomLong(fromMinusDays, toMinusDays)))
                .visibility(ALL_USERS)
                .build());
    }

    private Stream<Album> createAlbumsStreamWithTitlesAndCreatedAt(long fromId, long toId,
                                                                   long fromMinusDays, long toMinusDays,
                                                                   String title) {
        return LongStream.rangeClosed(fromId, toId)
            .mapToObj(id -> Album.builder()
                .id(id)
                .title(title)
                .createdAt(LocalDateTime.now().minusDays(getRandomLong(fromMinusDays, toMinusDays)))
                .build());
    }

    private long getRandomLong(long from, long to) {
        return random.nextLong(from, to);
    }

    private Album forUpdateAlbumTest(long userId, String title, String description) {
        long albumId = 2;
        Album album = Album.builder()
            .id(2)
            .title("Title")
            .description("Description")
            .authorId(1)
            .build();
        when(checker.findByIdWithPosts(albumId)).thenReturn(album);

        albumService.updateAlbum(userId, albumId, title, description);

        verify(checker, Mockito.times(1)).checkUserExists(userId);
        verify(checker, Mockito.times(1)).findByIdWithPosts(albumId);
        verify(albumRepository, Mockito.times(1)).save(album);
        return album;
    }

    @Test
    void testCreateAlbum_Success_All_Users() {
        long authorId = 1L;
        Album album = new Album();
        album.setVisibility(ALL_USERS);
        album.setTitle("title");
        List<Long> chosenUserIds = null;

        doNothing().when(checker).checkUserExists(authorId);
        doNothing().when(checker).checkAlbumExistsWithTitle(anyString(), eq(authorId));
        doNothing().when(checker).validateAlbumVisibility(any(), eq(chosenUserIds));

        when(albumRepository.save(any())).thenReturn(album);

        Album result = albumService.createAlbum(authorId, album, chosenUserIds);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(album);
    }

    @Test
    void testCreateAlbum_Success_Chosen_Users() {
        long authorId = 1L;
        Album album = new Album();
        album.setVisibility(CHOSEN_USERS);
        album.setTitle("title");
        List<Long> chosenUserIds = List.of(2L, 3L);

        doNothing().when(checker).checkUserExists(authorId);
        doNothing().when(checker).checkAlbumExistsWithTitle(anyString(), eq(authorId));
        doNothing().when(checker).validateAlbumVisibility(any(), eq(chosenUserIds));

        when(albumRepository.save(any())).thenReturn(album);

        Album result = albumService.createAlbum(authorId, album, chosenUserIds);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(album);
    }

    @Test
    void testUpdateAlbumVisibility_Success_Chosen_Users() {
        long userId = 1L;
        long albumId = 2L;
        AlbumVisibility visibility = CHOSEN_USERS;
        List<Long> chosenUserIds = List.of(2L, 3L);

        Album existedAlbum = new Album();
        Album updatedAlbum = new Album();

        when(checker.findByIdWithPosts(eq(albumId))).thenReturn(existedAlbum);
        doNothing().when(checker).validateAlbumVisibility(any(), eq(chosenUserIds));
        when(albumRepository.save(any())).thenReturn(updatedAlbum);

        Album result = albumService.updateAlbumVisibility(userId, albumId, visibility, chosenUserIds);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(updatedAlbum);
    }

    @Test
    void testGetAllAlbums_UserIsAuthor() {
        long userId = 1L;
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        Album existedAlbum = new Album();
        existedAlbum.setAuthorId(1L);

        doNothing().when(checker).checkUserExists(userId);
        when(albumRepository.findAll()).thenReturn(List.of(existedAlbum));

        List<Album> result = albumService.getAllAlbums(userId, albumFilterDto);

        Assert.assertEquals(1, result.size());
    }

    @Test
    void testGetAllAlbums_ALL_USERS() {
        long userId = 1L;
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        Album existedAlbum = new Album();
        existedAlbum.setAuthorId(2L);
        existedAlbum.setVisibility(ALL_USERS);

        doNothing().when(checker).checkUserExists(userId);
        when(albumRepository.findAll()).thenReturn(List.of(existedAlbum));

        List<Album> result = albumService.getAllAlbums(userId, albumFilterDto);

        Assert.assertEquals(1, result.size());
    }

    @Test
    void testGetAllAlbums_SUBSCRIBERS_ONLY() {
        long userId = 1L;
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        Album existedAlbum = new Album();
        existedAlbum.setAuthorId(2L);
        existedAlbum.setVisibility(SUBSCRIBERS_ONLY);

        doNothing().when(checker).checkUserExists(userId);
        when(albumRepository.findAll()).thenReturn(List.of(existedAlbum));
        when(userServiceClient.getFollowers(2L)).thenReturn(List.of(1L));

        List<Album> result = albumService.getAllAlbums(userId, albumFilterDto);

        Assert.assertEquals(1, result.size());
    }

    @Test
    void testGetAllAlbums_CHOSEN_USERS() {
        long userId = 1L;
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        Album existedAlbum = new Album();
        existedAlbum.setAuthorId(2L);
        existedAlbum.setVisibility(CHOSEN_USERS);
        existedAlbum.setChosenUsers(AlbumChosenUsers.builder().userIds(List.of(1L)).build());

        doNothing().when(checker).checkUserExists(userId);
        when(albumRepository.findAll()).thenReturn(List.of(existedAlbum));

        List<Album> result = albumService.getAllAlbums(userId, albumFilterDto);

        Assert.assertEquals(1, result.size());
    }

    @Test
    void testGetAllAlbums_AUTHOR_ONLY() {
        long userId = 1L;
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        Album existedAlbum = new Album();
        existedAlbum.setAuthorId(2L);
        existedAlbum.setVisibility(AUTHOR_ONLY);
        existedAlbum.setChosenUsers(AlbumChosenUsers.builder().userIds(List.of(1L)).build());

        doNothing().when(checker).checkUserExists(userId);
        when(albumRepository.findAll()).thenReturn(List.of(existedAlbum));

        List<Album> result = albumService.getAllAlbums(userId, albumFilterDto);

        Assert.assertEquals(0, result.size());
    }
}