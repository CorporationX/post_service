package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumAuthorFilter;
import faang.school.postservice.service.album.filter.AlbumCreatedFromFilter;
import faang.school.postservice.service.album.filter.AlbumCreatedToFilter;
import faang.school.postservice.service.album.filter.AlbumFilter;
import faang.school.postservice.service.album.filter.AlbumTitleFilter;
import faang.school.postservice.service.album.filter.MinimumOfPostsAtAlbum;
import faang.school.postservice.util.album.BuilderForAlbumsTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.ALREADY_FAVORITE;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.NOT_FAVORITE;
import static faang.school.postservice.util.album.BuilderForAlbumsTests.buildAlbum;
import static faang.school.postservice.util.album.BuilderForAlbumsTests.buildPost;
import static faang.school.postservice.util.album.BuilderForAlbumsTests.getRandomLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    private static final long USER_ID = 1;
    private static final long ALBUM_ID = 1;
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private AlbumServiceChecker checker;
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
    private Album album;

    @Test
    void createNewAlbum() {
        album = buildAlbum(TITLE, DESCRIPTION);

        albumService.createNewAlbum(USER_ID, album);

        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
        verify(checker, Mockito.times(1)).checkAlbumExistsWithTitle(album.getTitle(), USER_ID);
        verify(albumRepository, Mockito.times(1)).save(album);
    }

    @Test
    void getAlbum() {
        Album expected = new Album();
        when(checker.findByIdWithPosts(ALBUM_ID)).thenReturn(expected);
        album = albumService.getAlbum(USER_ID, ALBUM_ID);

        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
        verify(checker, Mockito.times(1)).findByIdWithPosts(ALBUM_ID);
        assertEquals(expected, album);
    }

    @Test
    void testUpdateAlbum() {
        String newTitle = "New title";
        String newDescription = "New description";

        album = forUpdateAlbumTest(newTitle, newDescription);

        verify(checker, Mockito.times(1)).checkAlbumExistsWithTitle(newTitle, USER_ID);
        assertEquals(album.getTitle(), newTitle);
        assertEquals(album.getDescription(), newDescription);
    }

    @Test
    void testUpdateAlbumWhenDescriptionIsNull() {
        String newTitle = "New title";
        String newDescription = null;

        album = forUpdateAlbumTest(newTitle, newDescription);

        verify(checker, Mockito.times(1)).checkAlbumExistsWithTitle(newTitle, USER_ID);
        assertEquals(album.getTitle(), newTitle);
        assertNotNull(album.getDescription());
    }

    @Test
    void testUpdateAlbumWhenEmptyTitle() {
        String newTitle = " ";
        String newDescription = "New description";

        album = forUpdateAlbumTest(newTitle, newDescription);

        assertNotEquals(album.getTitle(), newTitle);
        assertEquals(album.getDescription(), newDescription);
    }

    @Test
    void testDeleteAlbum() {
        album = buildAlbum(2, 1);
        when(checker.findByIdWithPosts(ALBUM_ID)).thenReturn(album);

        albumService.deleteAlbum(USER_ID, ALBUM_ID);

        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
        verify(checker, Mockito.times(1)).findByIdWithPosts(ALBUM_ID);
        verify(albumRepository, Mockito.times(1)).delete(album);
    }

    @Test
    void addAlbumToFavorites() {
        boolean isContains = true;
        when(checker.findByIdWithPosts(ALBUM_ID)).thenReturn(new Album());

        album = albumService.addAlbumToFavorites(USER_ID, ALBUM_ID);

        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
        verify(checker, Mockito.times(1))
                .checkFavoritesAlbumsContainsAlbum(USER_ID, album, ALREADY_FAVORITE, isContains);
        verify(albumRepository, Mockito.times(1)).addAlbumToFavorites(ALBUM_ID, USER_ID);
    }

    @Test
    void deleteAlbumFromFavorites() {
        album = new Album();
        boolean isContains = false;
        when(checker.findByIdWithPosts(ALBUM_ID)).thenReturn(album);

        albumService.deleteAlbumFromFavorites(USER_ID, ALBUM_ID);

        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
        verify(checker, Mockito.times(1))
                .checkFavoritesAlbumsContainsAlbum(USER_ID, album, NOT_FAVORITE, isContains);
        verify(albumRepository, Mockito.times(1)).deleteAlbumFromFavorites(ALBUM_ID, USER_ID);
    }

    @Test
    void addNewPosts() {
        List<Post> posts = new ArrayList<>(List.of(new Post()));
        album = buildAlbum(ALBUM_ID, TITLE, USER_ID, posts);
        List<Long> postsIds = List.of(1L, 4L);
        Post post1 = buildPost(postsIds.get(0));
        Post post2 = buildPost(postsIds.get(1));
        List<Post> postToAdd = List.of(post1, post2);
        when(checker.findByIdWithPosts(ALBUM_ID)).thenReturn(album);
        when(checker.isExistingPosts(1)).thenReturn(true);
        when(checker.isExistingPosts(4)).thenReturn(true);
        when(postRepository.findAllById(postsIds)).thenReturn(postToAdd);

        albumService.addNewPosts(USER_ID, ALBUM_ID, postsIds);

        assertEquals(album.getPosts().size(), 3);
        assertTrue(album.getPosts().contains(post1));
        assertTrue(album.getPosts().contains(post2));
        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
        verify(checker, Mockito.times(1)).findByIdWithPosts(ALBUM_ID);
        verify(albumRepository, Mockito.times(1)).save(album);
    }

    @Test
    void deletePosts() {
        Post post1 = buildPost(3);
        List<Post> expected = List.of(post1);
        List<Post> posts = LongStream.rangeClosed(1, 5)
                .mapToObj(BuilderForAlbumsTests::buildPost)
                .collect(Collectors.toCollection(ArrayList::new));
        List<Long> postIdsToDelete = List.of(4L, 2L, 5L, 1L);
        album = buildAlbum(ALBUM_ID, TITLE, USER_ID, posts);
        when(checker.findByIdWithPosts(ALBUM_ID)).thenReturn(album);

        albumService.deletePosts(USER_ID, ALBUM_ID, postIdsToDelete);

        assertEquals(album.getPosts().size(), 1);
        assertEquals(album.getPosts(), expected);
        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
        verify(checker, Mockito.times(1)).findByIdWithPosts(ALBUM_ID);
        verify(albumRepository, Mockito.times(1)).save(album);
    }

    @Test
    void getUserAlbums() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .title(TITLE)
                .createdFrom(LocalDateTime.now().minusMonths(1))
                .build();
        List<Album> albums = createAlbumsStreamWithTitlesAndCreatedAt(1, 10,
                1, 20, TITLE)
                .collect(Collectors.toCollection(ArrayList::new));
        List<Album> expected = new ArrayList<>(albums);
        createAlbumsStreamWithTitlesAndCreatedAt(11, 20,
                32, 50, TITLE)
                .forEach(albums::add);
        createAlbumsStreamWithTitlesAndCreatedAt(21, 30,
                1, 25, "Some else title")
                .forEach(albums::add);
        when(albumFilters.stream()).thenReturn(filters.stream());
        when(albumRepository.findByAuthorId(USER_ID)).thenReturn(albums);

        List<Album> result = albumService.getUserAlbums(USER_ID, albumFilterDto);

        assertEquals(expected, result);
        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
    }

    @Test
    void getFavoriteAlbums() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .minQuantityOfPosts(10)
                .build();
        List<Album> albums = createAlbumsStreamWithPosts(1, 10, 11, 30)
                .collect(Collectors.toCollection(ArrayList::new));
        List<Album> expected = new ArrayList<>(albums);
        createAlbumsStreamWithPosts(11, 20, 0, 9)
                .forEach(albums::add);
        when(albumFilters.stream()).thenReturn(filters.stream());
        when(albumRepository.findFavoriteAlbumsByUserId(USER_ID)).thenReturn(albums.stream());

        List<Album> result = albumService.getFavoriteAlbums(USER_ID, albumFilterDto);

        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
        assertEquals(expected, result);
    }

    @Test
    void getAllAlbums() {
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

        List<Album> result = albumService.getAllAlbums(USER_ID, albumFilterDto);

        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
        assertEquals(expected, result);
    }

    private Stream<Album> createAlbumsStreamWithPosts(long fromId, long toId,
                                                      long fromQuantityOfPosts, long toQuantityOfPosts) {
        List<Post> posts = LongStream.rangeClosed(1, getRandomLong(fromQuantityOfPosts, toQuantityOfPosts))
                .mapToObj(BuilderForAlbumsTests::buildPost)
                .toList();
        return LongStream.rangeClosed(fromId, toId)
                .mapToObj(id -> buildAlbum(id, TITLE, getRandomLong(1, 25), posts));
    }

    private Stream<Album> createAlbumsStreamWithAlbumsIdAndCreatedAt(long fromId, long toId,
                                                                     long fromMinusDays, long toMinusDays,
                                                                     long authorId) {
        return LongStream.rangeClosed(fromId, toId)
                .mapToObj(id -> buildAlbum(id, authorId, fromMinusDays, toMinusDays));
    }

    private Stream<Album> createAlbumsStreamWithTitlesAndCreatedAt(long fromId, long toId,
                                                                   long fromMinusDays, long toMinusDays,
                                                                   String title) {
        return LongStream.rangeClosed(fromId, toId)
                .mapToObj(id -> buildAlbum(id, title, fromMinusDays, toMinusDays));
    }

    private Album forUpdateAlbumTest(String newTitle, String newDescription) {
        album = buildAlbum(ALBUM_ID, TITLE, DESCRIPTION, USER_ID);
        when(checker.findByIdWithPosts(ALBUM_ID)).thenReturn(album);

        albumService.updateAlbum(USER_ID, ALBUM_ID, newTitle, newDescription);

        verify(checker, Mockito.times(1)).checkUserExists(USER_ID);
        verify(checker, Mockito.times(1)).findByIdWithPosts(ALBUM_ID);
        verify(albumRepository, Mockito.times(1)).save(album);
        return album;
    }
}