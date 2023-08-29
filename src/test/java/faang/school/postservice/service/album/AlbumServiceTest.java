package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumDtoResponse;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.dto.album.AuthorAlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidException;
import faang.school.postservice.filter.album.AlbumAuthorIdFilter;
import faang.school.postservice.filter.album.AlbumCreatedAtFilter;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.filter.album.AlbumTitleFilter;
import faang.school.postservice.filter.album.AlbumUpdateAtFilter;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private AlbumMapperImpl albumMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private AlbumService albumService;
    private Album album1;
    private Album album2;
    private Album album3;
    private Album album4;
    private List<AlbumFilter> albumFilter = new ArrayList<>();


    @BeforeEach
    void setUp() {
        album1 = Album.builder().id(1L).title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        album2 = Album.builder().id(1L).title(" ").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        album3 = Album.builder().id(1L).title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        album4 = Album.builder().id(1L).title("title").authorId(2L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();

        albumFilter.add(new AlbumAuthorIdFilter());
        albumFilter.add(new AlbumTitleFilter());
        albumFilter.add(new AlbumCreatedAtFilter());
        albumFilter.add(new AlbumUpdateAtFilter());
    }

    @Test
    void testCreateAlbum_DataValidException() {
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder().description("description").title("title").authorId(1L).build();
        assertThrows(DataValidException.class, () -> albumService.createAlbum(albumCreateDto));
    }

    @Test
    void testCreateAlbumUser_DataValidationException() {
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder().description("description").title("title").authorId(1L).build();

        when(userServiceClient.getUser(albumCreateDto.getAuthorId())).thenReturn(UserDto.builder().id(1L).build());
        when(albumRepository.existsByTitleAndAuthorId(albumCreateDto.getTitle(), albumCreateDto.getAuthorId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> albumService.createAlbum(albumCreateDto));
    }

    @Test
    void testCreateAlbum() {
        Album album = Album.builder().id(1L).build();
        AlbumDto albumDto = AlbumDto.builder().id(1L).build();
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder().description("description").title("title").authorId(1L).build();

        when(userServiceClient.getUser(albumCreateDto.getAuthorId())).thenReturn(UserDto.builder().id(1L).build());
        when(albumRepository.existsByTitleAndAuthorId(anyString(), anyLong())).thenReturn(false);

        when(albumMapper.toAlbumCreate(albumCreateDto)).thenReturn(album);
        when(albumMapper.toAlbumDto(album)).thenReturn(albumDto);
        when(albumRepository.save(album)).thenReturn(album);

        albumService.createAlbum(albumCreateDto);
        verify(albumRepository, times(1)).save(any());
    }

    @Test
    void testAddPostToAlbum() {
        Album album = Album.builder().id(2L).posts(new ArrayList<>(List.of(Post.builder().id(1L).albums(new ArrayList<>()).build()))).build();
        Album album2 = Album.builder().id(2L).posts(new ArrayList<>()).build();
        Post post = Post.builder().id(1L).albums(new ArrayList<>()).build();

        when(albumRepository.findById(2L)).thenReturn(Optional.of(album2));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(albumRepository.save(any())).thenReturn(album);

        albumService.addPostToAlbum(2L, 1L);
        verify(albumRepository).save(album);
    }

    @Test
    void testAddPostToAlbum_EntityNotFoundExceptionTwo() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.addPostToAlbum(1L, 1L));
    }
    @Test
    void testAddPostToAlbum_DataValidationException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(DataValidException.class, () -> albumService.addPostToAlbum(1L, 1L));
    }

    @Test
    void testDeletePostFromAlbum_EntityNotFoundException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> albumService.deletePostFromAlbum(1L, 1L));
    }

    @Test
    void testDeletePostFromAlbum_DataValidationException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(DataValidException.class, () -> albumService.deletePostFromAlbum(1L, 1L));
    }

    @Test
    void testDeletePostFromAlbum() {
        Post post = Post.builder().id(1L).build();
        Album album = Album.builder().id(1L).posts(new ArrayList<>(List.of(post))).build();
        Album album2 = Album.builder().id(1L).posts(new ArrayList<>()).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        albumService.deletePostFromAlbum(1L, 1L);
        verify(albumRepository).save(album2);
    }

    @Test
    void testAddAlbumToFavorites_EntityNotFoundException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> albumService.addAlbumToFavorites(1L));
    }

    @Test
    void testAddAlbumToFavorites() {
        UserDto userDto = UserDto.builder().id(1L).build();
        Album album = Album.builder().id(1L).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(userContext.getUserId()).thenReturn(1L);

        albumService.addAlbumToFavorites(1L);
        verify(albumRepository, times(1)).addAlbumToFavorites(album.getId(), userDto.getId());
    }

    @Test
    void testDeleteAlbumFromFavorites_EntityNotFoundException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> albumService.deleteAlbumFromFavorites(1L));
    }

    @Test
    void testDeleteAlbumFromFavorites() {
        UserDto userDto = UserDto.builder().id(1L).build();
        Album album = Album.builder().id(1L).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(userContext.getUserId()).thenReturn(1L);

        albumService.deleteAlbumFromFavorites(1L);
        verify(albumRepository, times(1)).deleteAlbumFromFavorites(album.getId(), userDto.getId());
    }

    @Test
    void testFindAlbumById_EntityNotFoundException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> albumService.findAlbumById(1L));
    }

    @Test
    void testFindAlbumById_UserIsNotAuthor() {
        AlbumDto albumDto = AlbumDto.builder().id(1L).authorId(0L).postsId(List.of()).build();

        when(userContext.getUserId()).thenReturn(1L);
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));

        assertEquals(albumDto, albumService.findAlbumById(1L));
    }

    @Test
    void testFindAlbumById_UserIsAuthor() {
        AuthorAlbumDto albumDto = AuthorAlbumDto.builder()
                .id(1L)
                .authorId(0L)
                .postsId(List.of())
                .visibility(AlbumVisibility.ALL_USERS)
                .build();

        when(userContext.getUserId()).thenReturn(0L);
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));

        assertEquals(albumDto, albumService.findAlbumById(1L));
    }

    @Test
    void testFindAListOfAllYourAlbums() {
        when(userContext.getUserId()).thenReturn(1L);
        when(albumRepository.findByAuthorId(1L)).thenReturn(Stream.of(
                album1, album2, album3, album4));
        albumService = new AlbumService(albumRepository, userServiceClient, albumMapper, userContext, postRepository, albumFilter);

        List<AlbumDtoResponse> albumByStatus = albumService.findAListOfAllYourAlbums(
                AlbumFilterDto.builder().title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build());
        assertEquals(2, albumByStatus.size());
    }

    @Test
    void testFindListOfAllAlbumsInTheSystem() {
        when(albumRepository.findAll()).thenReturn(List.of(
                album1, album2, album3, album4));
        albumService = new AlbumService(albumRepository, userServiceClient, albumMapper, userContext, postRepository, albumFilter);

        List<AlbumDtoResponse> albumByStatus = albumService.findListOfAllAlbumsInTheSystem(
                AlbumFilterDto.builder().title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build());
        assertEquals(2, albumByStatus.size());
    }

    @Test
    void testFindAListOfAllYourFavoriteAlbums() {
        when(userContext.getUserId()).thenReturn(1L);
        when(albumRepository.findFavoriteAlbumsByUserId(1L)).thenReturn(Stream.of(
                album1, album2, album3, album4));
        albumService = new AlbumService(albumRepository, userServiceClient, albumMapper, userContext, postRepository, albumFilter);

        List<AlbumDtoResponse> albumByStatus = albumService.findAListOfAllYourFavoriteAlbums(
                AlbumFilterDto.builder().title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build());
        assertEquals(2, albumByStatus.size());
    }

    @Test
    void testUpdateAlbumAuthorEntityNotFoundException() {
        AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder().title("title").description("description").build();

        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.updateAlbumAuthor(1L, albumUpdateDto));
    }

    @Test
    void testUpdateAlbumAuthorDataValidationException() {
        AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder().title("title").description("description").build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(DataValidException.class, () -> albumService.updateAlbumAuthor(1L, albumUpdateDto));
    }

    @Test
    void testUpdateAlbumAuthor() {
        AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder().title("title").description("description").build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));

        albumService.updateAlbumAuthor(1L, albumUpdateDto);
        verify(albumRepository, times(1)).save(any());
    }

    @Test
    void testDeleteAlbumFromEntityNotFoundException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> albumService.deleteAlbum(1L));
    }

    @Test
    void testDeleteAlbumFromDataValidationException() {
        Album album = Album.builder().id(1L).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(DataValidException.class, () -> albumService.deleteAlbum(1L));
    }

    @Test
    void testDeleteAlbum() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));

        albumService.deleteAlbum(1L);
        verify(albumRepository, times(1)).delete(any());
    }

    @Test
    void testFindListOfAllAlbumsInTheSystem_visibilityFiltrationFunctional() {
        album3.setUsersWithAccessIds(List.of(2L, 3L));
        album2.setTitle("title");
        UserDto author = UserDto.builder()
                .id(1L).followerIds(List.of(2L, 3L))
                .build();

        album1.setVisibility(AlbumVisibility.ONLY_SUBSCRIBERS);
        album2.setVisibility(AlbumVisibility.ONLY_AUTHOR);
        album3.setVisibility(AlbumVisibility.ONLY_SELECTED_BY_AUTHOR);
        album4.setVisibility(AlbumVisibility.ALL_USERS);

        when(userContext.getUserId()).thenReturn(4L);
        when(userServiceClient.getUser(anyLong())).thenReturn(author);
        when(albumRepository.findAll()).thenReturn(List.of(
                album1, album2, album3, album4));

        albumService = new AlbumService(albumRepository, userServiceClient, albumMapper, userContext, postRepository, albumFilter);

        List<AlbumDtoResponse> filteredAlbums = albumService.findListOfAllAlbumsInTheSystem(AlbumFilterDto.builder().title("title").build());
        assertEquals(1, filteredAlbums.size());
    }

    @Test
    void testFindListOfAllAlbumsInTheSystem_visibilityFiltrationFunctional_TheUserIsAuthor() {
        album3.setUsersWithAccessIds(List.of(2L, 3L));
        album2.setTitle("title");
        UserDto author = UserDto.builder()
                .id(1L).followerIds(List.of(2L, 3L))
                .build();

        album1.setVisibility(AlbumVisibility.ONLY_SUBSCRIBERS);
        album2.setVisibility(AlbumVisibility.ONLY_AUTHOR);
        album3.setVisibility(AlbumVisibility.ONLY_SELECTED_BY_AUTHOR);
        album4.setVisibility(AlbumVisibility.ALL_USERS);

        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(anyLong())).thenReturn(author);
        when(albumRepository.findAll()).thenReturn(List.of(
                album1, album2, album3, album4));

        albumService = new AlbumService(albumRepository, userServiceClient, albumMapper, userContext, postRepository, albumFilter);

        List<AlbumDtoResponse> filteredAlbums = albumService.findListOfAllAlbumsInTheSystem(AlbumFilterDto.builder().title("title").build());
        assertEquals(4, filteredAlbums.size());
    }

    @Test
    void testFindListOfAllAlbumsInTheSystem_visibilityFiltrationFunctional_TheUserIsSubscriber() {
        album3.setUsersWithAccessIds(List.of(3L));
        album2.setTitle("title");
        UserDto author = UserDto.builder()
                .id(1L).followerIds(List.of(2L, 3L))
                .build();

        album1.setVisibility(AlbumVisibility.ONLY_SUBSCRIBERS);
        album2.setVisibility(AlbumVisibility.ONLY_AUTHOR);
        album3.setVisibility(AlbumVisibility.ONLY_SELECTED_BY_AUTHOR);
        album4.setVisibility(AlbumVisibility.ALL_USERS);

        when(userContext.getUserId()).thenReturn(2L);
        when(userServiceClient.getUser(anyLong())).thenReturn(author);
        when(albumRepository.findAll()).thenReturn(List.of(
                album1, album2, album3, album4));

        albumService = new AlbumService(albumRepository, userServiceClient, albumMapper, userContext, postRepository, albumFilter);

        List<AlbumDtoResponse> filteredAlbums = albumService.findListOfAllAlbumsInTheSystem(AlbumFilterDto.builder().title("title").build());
        assertEquals(2, filteredAlbums.size());
    }

    @Test
    void testFindListOfAllAlbumsInTheSystem_visibilityFiltrationFunctional_TheUserHasAccessToAlbum() {
        album3.setUsersWithAccessIds(List.of(2L, 3L));
        album2.setTitle("title");
        UserDto author = UserDto.builder()
                .id(1L).followerIds(List.of(3L))
                .build();

        album1.setVisibility(AlbumVisibility.ONLY_SUBSCRIBERS);
        album2.setVisibility(AlbumVisibility.ONLY_AUTHOR);
        album3.setVisibility(AlbumVisibility.ONLY_SELECTED_BY_AUTHOR);
        album4.setVisibility(AlbumVisibility.ALL_USERS);

        when(userContext.getUserId()).thenReturn(2L);
        when(userServiceClient.getUser(anyLong())).thenReturn(author);
        when(albumRepository.findAll()).thenReturn(List.of(
                album1, album2, album3, album4));

        albumService = new AlbumService(albumRepository, userServiceClient, albumMapper, userContext, postRepository, albumFilter);

        List<AlbumDtoResponse> filteredAlbums = albumService.findListOfAllAlbumsInTheSystem(AlbumFilterDto.builder().title("title").build());
        assertEquals(2, filteredAlbums.size());
    }
}