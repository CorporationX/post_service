package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumDtoResponse;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.AlbumValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    private static final String EXPECTED_MESSAGE_ALBUM_NOT_FOUND = "Album not found";
    private static final String EXPECTED_MESSAGE_POST_NOT_FOUND = "Post not found";
    private static final String EXPECTED_MESSAGE_TITLE_MUST_BE_UNIQUE = "Title must be unique";
    private static final String EXPECTED_MESSAGE_TITLE_TOO_LONG = "Title must be less than 256 characters";
    private static final String EXPECTED_MESSAGE_AUTHOR_ID_CANNOT_BE_CHANGED = "AuthorId cannot be changed";
    private static final String EXPECTED_MESSAGE_DESCRIPTION_CANNOT_BE_NULL = "Description cannot be null";
    private static final String EXPECTED_ALBUM_ALREADY_IN_FAVORITES = "Album already in favorites";

    @InjectMocks
    private AlbumService albumService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumValidator albumValidator;
    @Mock
    private List<AlbumFilter> albumFilters;
    @Mock
    private UserContext userContext;
    @Spy
    private AlbumMapperImpl albumMapper;
    @Mock
    private UserServiceClient userServiceClient;

    private AlbumDto trueAlbumDto;
    private UserDto userDto;

    private long userId;
    private long albumId;
    private long postId;


    @BeforeEach
    void setUp() {
        trueAlbumDto = AlbumDto.builder().authorId(1L).title("title1").description("description2").build();
        userDto = UserDto.builder().id(1L).build();
        userId = 1L;
        albumId = 1L;
        postId = 1L;
    }

    @Test
    void testCreateAlbumSuccess() {
        Album album = albumMapper.toAlbum(trueAlbumDto);
        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.save(album)).thenReturn(album);
        AlbumDto albumDto = albumService.createAlbum(trueAlbumDto);

        assertAll(
                () -> assertNull(trueAlbumDto.getId()),
                () -> assertNotNull(albumDto.getId()),
                () -> assertEquals(trueAlbumDto.getAuthorId(), albumDto.getAuthorId()),
                () -> assertEquals(trueAlbumDto.getTitle(), albumDto.getTitle()),
                () -> assertEquals(trueAlbumDto.getDescription(), albumDto.getDescription())
        );
    }

    @Test
    void testCreateAlbumFailIfTitleIsNotUnique() {
        try {
            when(userContext.getUserId()).thenReturn(userId);
            when(albumRepository.existsByTitleAndAuthorId(trueAlbumDto.getTitle(), userDto.getId())).thenReturn(true);
            albumService.createAlbum(trueAlbumDto);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_TITLE_MUST_BE_UNIQUE);
        }
    }

    @Test
    void testCreateAlbumFailIfTitleIsTooLong() {
        try {
            when(userContext.getUserId()).thenReturn(userId);
            when(albumRepository.existsByTitleAndAuthorId(trueAlbumDto.getTitle(), userDto.getId())).thenReturn(false);
            albumService.createAlbum(trueAlbumDto);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_TITLE_TOO_LONG);
        }
    }

    @Test
    void testUpdateAlbumSuccess() {
        String content = "changed";
        AlbumDto updatedAlbumDto = AlbumDto.builder().id(1L).authorId(1L).title("title1").description(content).build();
        Album album = albumMapper.toAlbum(updatedAlbumDto);
        when(albumRepository.findById(updatedAlbumDto.getId())).thenReturn(Optional.of(album));
        when(albumRepository.existsByTitleAndAuthorId(updatedAlbumDto.getTitle(), userDto.getId())).thenReturn(false);

        albumService.updateAlbum(updatedAlbumDto);
        verify(albumValidator, times(1)).validationOfAlbumUpdate(updatedAlbumDto, album, false);

        assertEquals(content, album.getDescription());
    }

    @Test
    void testUpdateAlbumFailIfAlbumNotFound() {
        try {
            AlbumDto updatedAlbumDto = AlbumDto.builder().id(2L).authorId(1L).title("title1").description("changed").build();
            when(albumRepository.findById(updatedAlbumDto.getId())).thenReturn(Optional.empty());

            albumService.updateAlbum(updatedAlbumDto);
            doThrow(new EntityNotFoundException(EXPECTED_MESSAGE_ALBUM_NOT_FOUND)).when(albumRepository).findById(updatedAlbumDto.getId());
        } catch (EntityNotFoundException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_ALBUM_NOT_FOUND);
        }
    }

    @Test
    void testUpdateAlbumFailIfAuthorIdIsChanged() {
        try {
            String content = "changed";
            AlbumDto updatedAlbumDto = AlbumDto.builder().id(1L).authorId(2L).title("title1").description(content).build();
            Album album = albumMapper.toAlbum(updatedAlbumDto);
            when(albumRepository.findById(updatedAlbumDto.getId())).thenReturn(Optional.of(album));

            albumService.updateAlbum(updatedAlbumDto);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_AUTHOR_ID_CANNOT_BE_CHANGED);
        }
    }

    @Test
    void testUpdateAlbumFailIfDescriptionIsNull() {
        try {
            String content = "";
            AlbumDto updatedAlbumDto = AlbumDto.builder().id(1L).authorId(1L).title("title1").description(content).build();
            Album album = albumMapper.toAlbum(updatedAlbumDto);
            when(albumRepository.findById(updatedAlbumDto.getId())).thenReturn(Optional.of(album));
            when(albumRepository.existsByTitleAndAuthorId(updatedAlbumDto.getTitle(), userDto.getId())).thenReturn(false);

            albumService.updateAlbum(updatedAlbumDto);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_DESCRIPTION_CANNOT_BE_NULL);
        }
    }

    @Test
    void testUpdateAlbumFailIfTitleIsNotUnique() {
        try {
            Album album = albumMapper.toAlbum(trueAlbumDto);
            when(albumRepository.findById(trueAlbumDto.getId())).thenReturn(Optional.of(album));
            when(albumRepository.existsByTitleAndAuthorId(trueAlbumDto.getTitle(), userDto.getId())).thenReturn(true);

            albumService.updateAlbum(trueAlbumDto);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_TITLE_MUST_BE_UNIQUE);
        }
    }

    @Test
    void testUpdateAlbumFailIfTitleIsLong() {
        try {
            String title = getLongTitle();
            AlbumDto updatedAlbumDto = AlbumDto.builder().id(1L).authorId(1L).title(title).description("changed").build();
            Album album = albumMapper.toAlbum(updatedAlbumDto);
            when(albumRepository.findById(updatedAlbumDto.getId())).thenReturn(Optional.of(album));
            when(albumRepository.existsByTitleAndAuthorId(updatedAlbumDto.getTitle(), userDto.getId())).thenReturn(false);

            albumService.updateAlbum(updatedAlbumDto);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_TITLE_TOO_LONG);
        }
    }

    @Test
    void testDeleteAlbumSuccess() {
        albumService.deleteAlbum(trueAlbumDto.getId());
        verify(albumRepository, times(1)).deleteById(trueAlbumDto.getId());
    }

    @Test
    void testDeleteAlbumFailIfAlbumNotFound() {
        try {
            albumService.deleteAlbum(2L);
        } catch (EntityNotFoundException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_ALBUM_NOT_FOUND);
        }
    }

    @Test
    public void testAddPostSuccess() {
        Album album = Album.builder().id(albumId).authorId(userId).posts(new ArrayList<>()).build();
        Post post = new Post();

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findByAuthorId(userDto.getId())).thenReturn(Stream.of(album));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(albumRepository.save(album)).thenReturn(album);
        when(albumMapper.toDto(album)).thenReturn(new AlbumDto());

        AlbumDto result = albumService.addPost(albumId, postId);

        assertNotNull(result);
        assertEquals(album.getPosts().size(), 1);
    }

    @Test()
    public void testAddPostFailIfAlbumNotFound() {
        try {
            albumService.addPost(albumId, postId);
            when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
            doThrow(new EntityNotFoundException(EXPECTED_MESSAGE_ALBUM_NOT_FOUND)).when(albumRepository).getReferenceById(albumId);
        } catch (EntityNotFoundException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_ALBUM_NOT_FOUND);
        }
    }

    @Test()
    public void testAddPostFailIfPostNotFound() {
        Album album = Album.builder().id(albumId).authorId(userId).posts(new ArrayList<>()).build();

        try {
            when(userContext.getUserId()).thenReturn(userId);
            when(albumRepository.findByAuthorId(userDto.getId())).thenReturn(Stream.of(album));
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            albumService.addPost(albumId, postId);
        } catch (EntityNotFoundException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_POST_NOT_FOUND);
        }
    }

    @Test
    public void testDeletePost() {
        Post post = Post.builder().id(postId).build();
        List<Post> posts = new ArrayList<>();
        posts.add(post);
        Album album = Album.builder().id(albumId).authorId(userId).posts(posts).build();


        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findByAuthorId(userDto.getId())).thenReturn(Stream.of(album));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(albumRepository.save(album)).thenReturn(album);
        when(albumMapper.toDto(album)).thenReturn(new AlbumDto());

        AlbumDto result = albumService.deletePost(albumId, postId);

        assertNotNull(result);
        assertFalse(album.getPosts().contains(post));
    }

    @Test()
    public void testDeletePostFailIfAlbumNotFound() {
        try {
            when(userContext.getUserId()).thenReturn(userId);
            when(albumRepository.findByAuthorId(userDto.getId())).thenReturn(Stream.empty());
            albumService.deletePost(albumId, postId);
        } catch (EntityNotFoundException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_ALBUM_NOT_FOUND);
        }
    }

    @Test()
    public void testDeletePostFailIfPostNotFound() {
        Album album = Album.builder().id(albumId).authorId(userId).posts(new ArrayList<>()).build();

        try {
            when(userContext.getUserId()).thenReturn(userId);
            when(albumRepository.findByAuthorId(userDto.getId())).thenReturn(Stream.of(album));
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            albumService.deletePost(albumId, postId);
        } catch (EntityNotFoundException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_POST_NOT_FOUND);
        }
    }

    @Test
    public void testGetAlbumSuccess() {
        Album album = new Album();
        AlbumDto albumDto = new AlbumDto();
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(albumMapper.toDto(album)).thenReturn(albumDto);
        when(userContext.getUserId()).thenReturn(userId);

        AlbumDtoResponse result = albumService.getAlbum(albumId);

        assertNotNull(result);
        assertSame(albumDto, result);
    }

    @Test
    public void testGetAlbumNotFound() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.getAlbum(albumId));
    }

    @Test
    public void testGetMyAlbums() {
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        List<Album> albums = List.of(
                Album.builder().id(1L).authorId(userId).title("Album 1").visibility(AlbumVisibility.PUBLIC).build(),
                Album.builder().id(2L).authorId(userId).title("Album 2").visibility(AlbumVisibility.PUBLIC).build()
        );

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findByAuthorId(userId)).thenReturn(albums.stream());

        List<AlbumDtoResponse> result = albumService.getMyAlbums(albumFilterDto);

        assertEquals(2, result.size());
    }

    @Test
    public void testGetMyAlbumsNoAlbumsFound() {
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findByAuthorId(userId)).thenReturn(Stream.empty());

        List<AlbumDtoResponse> result = albumService.getMyAlbums(albumFilterDto);

        assertTrue(result.isEmpty());

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findByAuthorId(userId);
    }

    @Test
    public void testAddAlbumToFavoriteSuccess() {
        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findByAuthorId(userId)).thenReturn(Stream.of(Album.builder().id(albumId).build()));
        when(albumRepository.existInFavorites(albumId, userId)).thenReturn(false);
        albumService.addAlbumToFavourite(albumId);
        verify(albumRepository, times(1)).existInFavorites(albumId, userId);
        verify(albumRepository, times(1)).addAlbumToFavorites(albumId, userId);
    }

    @Test
    public void testAddAlbumToFavoriteFailIfAlbumNotFound() {
        try {
            when(userContext.getUserId()).thenReturn(userId);
            when(albumRepository.findByAuthorId(userId)).thenReturn(Stream.empty());
            albumService.addAlbumToFavourite(albumId);
        } catch (EntityNotFoundException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_ALBUM_NOT_FOUND);
        }
    }

    @Test
    public void testAddAlbumToFavoriteFailIfAlreadyInFavorites() {
        Album album = Album.builder().id(albumId).authorId(userId).build();
        try {
            when(userContext.getUserId()).thenReturn(userId);
            when(albumRepository.findByAuthorId(userId)).thenReturn(Stream.of(album));
            when(albumRepository.existInFavorites(albumId, userId)).thenReturn(true);
            albumService.addAlbumToFavourite(albumId);
        } catch (DataValidationException e) {
            assertEquals(e.getMessage(), EXPECTED_ALBUM_ALREADY_IN_FAVORITES);
        }
    }

    private String getLongTitle() {
        return "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut " +
                "laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation " +
                "ullamcorper suscipit lobortis nisl ut aliquip ex ea com";
    }

    @Test
    void testGetAllAlbumsByFilter_visibilityFilter() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder().title("Album").build();
        UserDto author = UserDto.builder().id(1L).idUsersWithAccess(List.of(2L, 3L)).build();
        Album album1 = Album.builder().id(1L).authorId(1L).title("Album").visibility(AlbumVisibility.ONLY_SUBSCRIBERS).build();
        Album album2 = Album.builder().id(2L).authorId(2L).title("Album").visibility(AlbumVisibility.ONLY_SELECTED).build();
        Album album3 = Album.builder().id(3L).authorId(1L).title("Album").visibility(AlbumVisibility.PUBLIC).build();
        Album album4 = Album.builder().id(4L).authorId(2L).title("Album").visibility(AlbumVisibility.PRIVATE).build();
        album3.setIdUsersWithAccess(List.of(2L, 3L));

        when(userContext.getUserId()).thenReturn(2L);
        when(userServiceClient.getUser(anyLong())).thenReturn(author);
        when(albumRepository.findAll()).thenReturn(List.of(album1, album2, album3, album4));

        albumService = new AlbumService(albumMapper, albumRepository, postRepository, albumValidator,
                userContext, albumFilters, userServiceClient);
        List<AlbumDtoResponse> result = albumService.getAlbumsByFilter(albumFilterDto);
        assertEquals(4, result.size());
    }
}