package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.album_filter.AlbumAuthorIdFilter;
import faang.school.postservice.filter.album_filter.AlbumCreatedAtFilter;
import faang.school.postservice.filter.album_filter.AlbumFilter;
import faang.school.postservice.filter.album_filter.AlbumTitleFilter;
import faang.school.postservice.filter.album_filter.AlbumUpdateAtFilter;
import faang.school.postservice.mapper.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
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

    @Test
    void testCreateAlbumDataValidationException() {
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder().description("description").title("title").authorId(1L).build();
        assertThrows(DataValidationException.class, () -> albumService.createAlbum(albumCreateDto));
    }

    @Test
    void testCreateAlbumUserDataValidationException() {
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
    void testAddPostToAlbumEntityNotFoundException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> albumService.addPostToAlbum(1L, 1L));
    }

    @Test
    void testAddPostToAlbumDataValidationException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(DataValidationException.class, () -> albumService.addPostToAlbum(1L, 1L));
    }

    @Test
    void testAddPostToAlbumEntityNotFoundExceptionTwo() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.addPostToAlbum(1L, 1L));
    }

    @Test
    void testAddPostToAlbumDataValidationExceptionTwo() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));
        when(postRepository.findById(1L)).thenReturn(Optional.of(Post.builder().id(1L).albums(List.of(Album.builder().id(1L).build())).build()));

        assertThrows(DataValidationException.class, () -> albumService.addPostToAlbum(1L, 1L));
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
    void testDeletePostFromAlbumEntityNotFoundException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> albumService.deletePostFromAlbum(1L, 1L));
    }

    @Test
    void testDeletePostFromAlbumDataValidationException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(DataValidationException.class, () -> albumService.deletePostFromAlbum(1L, 1L));
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
    void testAddAlbumToFavoritesEntityNotFoundException() {
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
    void testDeleteAlbumFromFavoritesEntityNotFoundException() {
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
    void testFindByIdWithPostsEntityNotFoundException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> albumService.findByIdWithPosts(1L));
    }

    @Test
    void testFindByIdWithPosts() {
        AlbumDto albumDto = AlbumDto.builder().id(1L).authorId(0L).postsId(List.of()).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));

        assertEquals(albumDto, albumService.findByIdWithPosts(1L));
    }

    @Test
    void testFindAListOfAllYourAlbums() {
        Album album1 = Album.builder().id(1L).title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        Album album2 = Album.builder().id(1L).title(" ").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        Album album3 = Album.builder().id(1L).title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        Album album4 = Album.builder().id(1L).title("title").authorId(2L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();

        List<AlbumFilter> albumFilter = new ArrayList<>();
        albumFilter.add(new AlbumAuthorIdFilter());
        albumFilter.add(new AlbumTitleFilter());
        albumFilter.add(new AlbumCreatedAtFilter());
        albumFilter.add(new AlbumCreatedAtFilter());

        when(userContext.getUserId()).thenReturn(1L);
        when(albumRepository.findByAuthorId(1L)).thenReturn(Stream.of(
                album1, album2, album3, album4));
        albumService = new AlbumService(albumRepository, userServiceClient, albumMapper, userContext, postRepository, albumFilter);

        List<AlbumDto> albumByStatus = albumService.findAListOfAllYourAlbums(
                AlbumFilterDto.builder().title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build());
        assertEquals(2, albumByStatus.size());
    }

    @Test
    void testFindListOfAllAlbumsInTheSystem() {
        Album album1 = Album.builder().id(1L).title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        Album album2 = Album.builder().id(1L).title(" ").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        Album album3 = Album.builder().id(1L).title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        Album album4 = Album.builder().id(1L).title("title").authorId(2L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();

        List<AlbumFilter> albumFilter = new ArrayList<>();
        albumFilter.add(new AlbumAuthorIdFilter());
        albumFilter.add(new AlbumTitleFilter());
        albumFilter.add(new AlbumCreatedAtFilter());
        albumFilter.add(new AlbumCreatedAtFilter());

        when(albumRepository.findAll()).thenReturn(List.of(
                album1, album2, album3, album4));
        albumService = new AlbumService(albumRepository, userServiceClient, albumMapper, userContext, postRepository, albumFilter);

        List<AlbumDto> albumByStatus = albumService.findListOfAllAlbumsInTheSystem(
                AlbumFilterDto.builder().title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build());
        assertEquals(2, albumByStatus.size());
    }

    @Test
    void testFindAListOfAllYourFavoriteAlbums() {
        Album album1 = Album.builder().id(1L).title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        Album album2 = Album.builder().id(1L).title(" ").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        Album album3 = Album.builder().id(1L).title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        Album album4 = Album.builder().id(1L).title("title").authorId(2L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();

        List<AlbumFilter> albumFilter = new ArrayList<>();
        albumFilter.add(new AlbumAuthorIdFilter());
        albumFilter.add(new AlbumTitleFilter());
        albumFilter.add(new AlbumCreatedAtFilter());
        albumFilter.add(new AlbumCreatedAtFilter());

        when(userContext.getUserId()).thenReturn(1L);
        when(albumRepository.findFavoriteAlbumsByUserId(1L)).thenReturn(Stream.of(
                album1, album2, album3, album4));
        albumService = new AlbumService(albumRepository, userServiceClient, albumMapper, userContext, postRepository, albumFilter);

        List<AlbumDto> albumByStatus = albumService.findAListOfAllYourFavoriteAlbums(
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

        assertThrows(DataValidationException.class, () -> albumService.updateAlbumAuthor(1L, albumUpdateDto));
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

        assertThrows(DataValidationException.class, () -> albumService.deleteAlbum(1L));
    }

    @Test
    void testDeleteAlbum() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(Album.builder().id(1L).build()));

        albumService.deleteAlbum(1L);
        verify(albumRepository, times(1)).delete(any());
    }
}