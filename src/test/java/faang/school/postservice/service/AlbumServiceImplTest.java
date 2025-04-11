package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.AlbumUsersDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.AlbumAccessDeniedException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.filter.albumvisibility.AlbumVisibilityFilter;
import faang.school.postservice.mapper.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.album.AlbumValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlbumServiceImpl Test")
class AlbumServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private AlbumVisibilityFilter allUsersFilter;
    @Mock
    private AlbumVisibilityFilter followersFilter;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private AlbumValidator albumValidator;
    @Spy
    private AlbumMapperImpl albumMapper;

    @Captor
    private ArgumentCaptor<Album> albumCaptor;

    @InjectMocks
    private AlbumServiceImpl albumService;

    private final Long userId = 1L;
    private final Long postId = 1L;
    private final Long albumId = 1L;
    AlbumDto albumDto = new AlbumDto();
    AlbumFilterDto filters = new AlbumFilterDto();
    Album album = new Album();
    Post post = new Post();
    AlbumFilter mockFilter = mock(AlbumFilter.class);


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
        album.setCreatedAt(LocalDateTime.now());
        album.setPosts(new ArrayList<>(List.of(post)));
    }

    void setAlbumFilterDto() {
        filters.setTitle("Title");
        filters.setCreatedAtAfter(LocalDateTime.now().minusDays(2));
    }

    @BeforeEach
    public void init() {
        when(allUsersFilter.getAlbumVisibility()).thenReturn(AlbumVisibility.PUBLIC);
        when(followersFilter.getAlbumVisibility()).thenReturn(AlbumVisibility.FOLLOWERS);

        albumService = new AlbumServiceImpl(
                albumRepository,
                userContext,
                List.of(allUsersFilter, followersFilter),
                userServiceClient,
                albumMapper,
                postRepository,
                List.of(mockFilter),
                albumValidator
        );

        albumService.initAlbumVisibilities();
    }

    @Nested
    @DisplayName("Get Album Tests")
    class GetAlbumTests {

        @Test
        @DisplayName("Should return album when found by ID")
        public void testGetAlbumById() {
            long albumId = 1L;
            when(albumRepository.findById(albumId)).thenReturn(Optional.of(createAlbum(albumId, 1L, AlbumVisibility.PUBLIC)));
            when(allUsersFilter.apply(createAlbum(albumId, 1L, AlbumVisibility.PUBLIC))).thenReturn(createAlbumResponseDto(albumId));

            AlbumResponseDto result = albumService.getAlbumById(albumId);

            assertNotNull(result);
            assertEquals(albumId, result.id());
            verify(albumRepository).findById(albumId);
            verify(allUsersFilter).apply(createAlbum(albumId, 1L, AlbumVisibility.PUBLIC));
        }

        @Test
        @DisplayName("Should throw exception when album not found")
        public void testGetAlbumByIdWhenAlbumNotFound() {
            long albumId = 1L;
            when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> albumService.getAlbumById(albumId));
            verify(albumRepository).findById(albumId);
        }

        @Test
        @DisplayName("Should return albums when found by author ID")
        public void testGetAlbumsByAuthorId() {
            long authorId = 1L;

            when(albumRepository.findByAuthorId(authorId)).thenReturn(List.of(createAlbum(1L, 1L, AlbumVisibility.PUBLIC),
                    createAlbum(2L, 1L, AlbumVisibility.FOLLOWERS)));
            when(allUsersFilter.apply(createAlbum(1L, 1L, AlbumVisibility.PUBLIC))).thenReturn(createAlbumResponseDto(1L));
            when(followersFilter.apply(createAlbum(2L, 1L, AlbumVisibility.FOLLOWERS))).thenReturn(createAlbumResponseDto(2L));

            List<AlbumResponseDto> result = albumService.getAlbumsByAuthorId(authorId);

            assertEquals(2, result.size());
            verify(albumRepository).findByAuthorId(authorId);
            verify(allUsersFilter).apply(createAlbum(1L,1L, AlbumVisibility.PUBLIC));
            verify(followersFilter).apply(createAlbum(2L, 1L, AlbumVisibility.FOLLOWERS));
        }
    }

    @Nested
    @DisplayName("Update Album Visibility Tests")
    class UpdateAlbumVisibilityTests {

        @Test
        @DisplayName("Should update album visibility if user is author")
        public void testUpdateAlbumVisibility() {
            long albumId = 1L;
            long userId = 1L;
            Album album = createAlbum(1L, 1L, AlbumVisibility.PUBLIC);
            when(albumRepository.findById(albumId))
                    .thenReturn(Optional.of(album));
            when(userContext.getUserId()).thenReturn(userId);

            Album updatedAlbum = createAlbum(1L, 1L, AlbumVisibility.FOLLOWERS);
            when(albumRepository.save(any(Album.class))).thenReturn(updatedAlbum);

            albumService.updateAlbumVisibility(albumId, AlbumVisibility.FOLLOWERS);

            assertEquals(AlbumVisibility.FOLLOWERS, updatedAlbum.getAlbumVisibility());
            verify(albumRepository).save(any(Album.class));
        }

        @Test
        @DisplayName("Should throw exception if user is not the author")
        public void testUpdateAlbumVisibilityWhenUserIsNotAuthor() {
            long albumId = 1L;
            long userId = 1L;
            long otherUserId = 2L;
            Album album = createAlbum(albumId, otherUserId, AlbumVisibility.PRIVATE);
            when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
            when(userContext.getUserId()).thenReturn(userId);

            assertThrows(AlbumAccessDeniedException.class,
                    () -> albumService.updateAlbumVisibility(albumId, AlbumVisibility.FOLLOWERS));
            verify(albumRepository, never()).save(album);
        }
    }

    @Nested
    @DisplayName("Add Users for Access Tests")
    class AddUsersForAccessTests {

        @Test
        @DisplayName("Should add users when album has correct visibility")
        public void testAddUsersForAccessAlbum() {
            long albumId = 1L;
            long userId = 1L;
            Album album = createAlbum(albumId, userId, AlbumVisibility.SELECTED);
            AlbumUsersDto albumUsersDto = new AlbumUsersDto(List.of(2L, 3L));
            when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
            when(userContext.getUserId()).thenReturn(userId);

            albumService.addUsersForAccessAlbum(albumId, albumUsersDto);

            verify(albumRepository).addUserForVisibilityAtAlbum(albumId, 2L);
            verify(albumRepository).addUserForVisibilityAtAlbum(albumId, 3L);
        }

        @Test
        @DisplayName("Should throw exception when album visibility is not SELECTED")
        public void testAddUsersForAccessAlbumWhenWrongVisibility() {
            long albumId = 1L;
            long userId = 1L;
            Album album = createAlbum(albumId, userId, AlbumVisibility.PUBLIC);
            AlbumUsersDto albumUsersDto = new AlbumUsersDto(List.of(2L, 3L));
            when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
            when(userContext.getUserId()).thenReturn(userId);

            assertThrows(IllegalArgumentException.class, () -> albumService.addUsersForAccessAlbum(albumId, albumUsersDto));
            verify(albumRepository, never()).addUserForVisibilityAtAlbum(anyLong(), anyLong());
        }
    }

    private AlbumResponseDto createAlbumResponseDto(long albumId) {
        return new AlbumResponseDto(albumId, "album", null, 1L);
    }

    private static Album createAlbum(long albumId, long authorId, AlbumVisibility albumVisibility) {
        return Album.builder()
                .id(albumId)
                .albumVisibility(albumVisibility)
                .authorId(authorId)
                .build();
    }

    @Test
    void testCreateAlbum() {
        setUp();
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
        setUp();
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
        setUp();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                albumService.deletePostFromAlbum(userId, postId, albumId));
    }

    @Test
    void testDeletePostFromAlbumWithEmptyPost() {
        setUp();
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                albumService.deletePostFromAlbum(userId, postId, albumId));
    }

    @Test
    void testGetAlbumById() {
        setUp();
        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.of(album));

        AlbumDto result = albumService.getAlbumByIdReturnAlbumDto(albumId);

        assertNotNull(result);
        assertEquals(albumDto.getTitle(), result.getTitle());
    }

    @Test
    void testUpdateAlbumReturnUpdatedAlbumDto() {
        setUp();
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
        setUp();
        setAlbumFilterDto();

        when(albumRepository.findByAuthorId(userId)).thenReturn(List.of(album));

        List<AlbumDto> result = albumService.getAllAlbumsByAuthorIdWithFilters(userId, filters);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
    }

    @Test
    void testGetAllAlbumsWithFilters() {
        setUp();
        setAlbumFilterDto();

        album.setCreatedAt(LocalDateTime.now().minusDays(1));
        when(albumRepository.findAllAlbums()).thenReturn(List.of(album));

        List<AlbumDto> result = albumService.getAllAlbumsWithFilters(filters);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testAddFavouriteAlbum() {
        setUp();
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, "Name", "email"));

        albumService.addFavouriteAlbum(userId, albumId);

        verify(albumRepository, times(1)).addAlbumToFavorites(albumId, userId);
    }

    @Test
    void testAddFavouriteAlbumAndThrowException() {
        setUp();
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.addFavouriteAlbum(userId, albumId));
    }

    @Test
    void testDeleteFavouriteAlbum() {
        setUp();
        albumService.deleteFavouriteAlbum(userId, albumId);
        verify(albumRepository, times(1)).deleteAlbumFromFavorites(albumId, userId);
    }

    @Test
    void testGetFavouriteAlbumsByUserId() {
        setUp();
        setAlbumFilterDto();
        album.setCreatedAt(LocalDateTime.now().minusDays(2));
        when(albumRepository.findFavoriteAlbumsByUserId(userId)).thenReturn(List.of(album));

        List<AlbumDto> result = albumService.getFavouriteAlbumsByUserId(userId, filters);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
    }

}