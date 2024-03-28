package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilter;
import faang.school.postservice.validation.album.AlbumValidator;
import faang.school.postservice.validation.user.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    private AlbumRepository albumRepository;
    private AlbumMapper albumMapper;
    private AlbumValidator albumValidator;
    private UserValidator userValidator;
    private PostRepository postRepository;
    private List<AlbumFilter> albumFilters;
    private AlbumService albumService;

    private AlbumFilter albumFilter;
    private AlbumFilterDto albumFilterDto;
    private Album album;
    private AlbumDto albumDto;
    private Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .content("Valid post content")
                .authorId(20L)
                .build();
        album = Album.builder()
                .id(10L)
                .title("Valid album title")
                .description("Valid album description")
                .authorId(20L)
                .posts(new ArrayList<>(List.of()))
                .build();
        albumDto = AlbumDto.builder()
                .id(album.getId())
                .title(album.getTitle())
                .description(album.getDescription())
                .authorId(album.getAuthorId())
                .postsIds(new ArrayList<>(List.of()))
                .build();
        albumFilterDto = AlbumFilterDto.builder()
                .title("album")
                .build();

        albumRepository = mock(AlbumRepository.class);
        albumMapper = spy(AlbumMapperImpl.class);
        albumValidator = mock(AlbumValidator.class);
        userValidator = mock(UserValidator.class);
        postRepository = mock(PostRepository.class);
        albumFilter = mock(AlbumFilter.class);
        albumFilters = List.of(albumFilter);

        albumService = new AlbumService(albumRepository, albumMapper, albumValidator, userValidator,
                postRepository, albumFilters);
    }

    @Test
    void create_AlbumCreatedAndSavedToDb_ThenReturnedAsDto() {
        album.setAlbumVisibility(AlbumVisibility.PUBLIC);
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        AlbumDto returned = albumService.create(albumDto.getAuthorId(), albumDto);

        assertAll(
                () -> verify(albumRepository, times(1)).save(any(Album.class)),
                () -> verifyValidateAlbumTitle(1),
                () -> verify(albumMapper, times(1)).toEntity(albumDto),
                () -> verify(albumMapper, times(1)).toDto(album),
                () -> assertEquals(albumDto, returned)
        );
    }

    @Test
    void getAlbum_AlbumFound_ThenReturnedAsDto() {
        whenAlbumRepositoryFindById(album.getId());

        AlbumDto returned = albumService.getAlbum(albumDto.getId());

        assertAll(
                () -> verifyAlbumRepositoryFindById(1, albumDto.getId()),
                () -> assertEquals(albumDto, returned)
        );
    }

    @Test
    void getAlbum_AlbumNotFound_ShouldThrowEntityNotFoundException() {
        when(albumRepository.findById(albumDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> albumService.getAlbum(albumDto.getId()));
    }

    @Test
    void getUsersAlbums_AlbumsFoundAndFiltered_ThenReturnedAsDto() {
        when(albumRepository.findByAuthorId(20L)).thenReturn(Stream.ofNullable(album));
        whenFilterIsApplicable();

        List<AlbumDto> returned = albumService.getUsersAlbums(20L, albumFilterDto);

        assertAll(
                () -> verify(albumRepository, times(1)).findByAuthorId(20L),
                () -> verify(albumFilter, times(1)).isApplicable(albumFilterDto),
                () -> verify(albumFilter, times(1)).apply(List.of(album), albumFilterDto),
                () -> verify(albumMapper, times(1)).toDto(List.of(album)),
                () -> assertEquals(List.of(albumDto), returned)
        );
    }

    @Test
    void getAllAlbums_AlbumsFoundAndFiltered_ThenReturnedAsDto() {
        when(albumRepository.findAll()).thenReturn(List.of(album));
        whenFilterIsApplicable();

        List<AlbumDto> returned = albumService.getAllAlbums(albumFilterDto);

        assertAll(
                () -> verify(albumRepository, times(1)).findAll(),
                () -> verifyFilterIsApplicable(1),
                () -> verifyFilterIsApplied(1),
                () -> verify(albumMapper, times(1)).toDto(List.of(album)),
                () -> assertEquals(List.of(albumDto), returned)
        );
    }

    @Test
    void getFavouriteAlbums_AlbumsFoundAndFiltered_ThenReturnedAsDto() {
        when(albumRepository.findFavoriteAlbumsByUserId(20L)).thenReturn(Stream.ofNullable(album));
        whenFilterIsApplicable();

        List<AlbumDto> returned = albumService.getFavouriteAlbums(20L, albumFilterDto);

        assertAll(
                () -> verify(albumRepository, times(1)).findFavoriteAlbumsByUserId(20L),
                () -> verifyFilterIsApplicable(1),
                () -> verifyFilterIsApplied(1),
                () -> verify(albumMapper, times(1)).toDto(List.of(album)),
                () -> assertEquals(List.of(albumDto), returned)
        );
    }

    @Test
    void update_AlbumUpdatedAndSavedToDb_ThenReturnedAsDto() {
        whenAlbumRepositoryFindById(albumDto.getId());
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        AlbumDto returned = albumService.update(20L, albumDto);

        assertAll(
                () -> verifyAlbumRepositoryFindById(1, albumDto.getId()),
                () -> verifyValidateIfUserIsAuthor(1, 20L),
                () -> verifyValidateUpdatedAlbum(1, 20L),
                () -> verifyValidateAlbumTitle(1),
                () -> verify(albumMapper, times(1)).toEntity(albumDto),
                () -> verify(albumRepository, times(1)).save(albumMapper.toEntity(albumDto)),
                () -> verify(albumMapper, times(1)).toDto(album),
                () -> assertEquals(albumDto, returned)
        );
    }

    @Test
    void addPostToAlbum_PostAddedAndAlbumSavedToDb_ThenReturnedAsDto() {
        whenAlbumRepositoryFindById(album.getId());
        whenPostRepositoryFindById(post.getId());
        when(albumRepository.save(album)).thenReturn(album);

        AlbumDto returned = albumService.addPostToAlbum(20L, album.getId(), post.getId());

        assertAll(
                () -> verifyAlbumRepositoryFindById(1, album.getId()),
                () -> verifyValidateIfUserIsAuthor(1, 20L),
                () -> verifyPostRepositoryFindById(1, post.getId()),
                () -> verify(albumRepository, times(1)).save(album),
                () -> verify(albumMapper, times(1)).toDto(album),
                () -> assertEquals(1, returned.getPostsIds().size()),
                () -> assertEquals(1L, returned.getPostsIds().get(0)),
                () -> assertEquals(post, album.getPosts().get(0)),
                () -> assertNotEquals(albumDto, returned)
        );
    }

    @Test
    void addAlbumToFavourites_AlbumAddedToFavourites_IsValid() {
        when(albumRepository.findById(album.getId())).thenReturn(Optional.ofNullable(album));
        albumService.addAlbumToFavourites(album.getAuthorId(), album.getId());

        assertAll(
                () -> verifyValidateUserExists(1, 20L),
                () -> verify(albumRepository, times(1)).addAlbumToFavorites(
                        album.getId(), album.getAuthorId())
        );
    }

    @Test
    void deletePostFromAlbum_PostDeletedAndAlbumSavedToDb_ThenReturnedAsDto() {
        album.getPosts().add(post);
        albumDto.getPostsIds().add(post.getId());
        whenAlbumRepositoryFindById(album.getId());
        when(albumRepository.save(album)).thenReturn(album);

        AlbumDto returned = albumService.deletePostFromAlbum(20L, album.getId(), post.getId());

        assertAll(
                () -> verifyAlbumRepositoryFindById(1, album.getId()),
                () -> verifyValidateIfUserIsAuthor(1, 20L),
                () -> verify(albumRepository, times(1)).save(album),
                () -> verify(albumMapper, times(1)).toDto(album),
                () -> assertEquals(Collections.emptyList(), album.getPosts()),
                () -> assertEquals(Collections.emptyList(), returned.getPostsIds()),
                () -> assertNotEquals(albumDto, returned)
        );
    }

    @Test
    void deleteAlbumFromFavourites_AlbumIsDeletedFromFavourites_IsValid() {
        when(albumRepository.findById(albumDto.getId())).thenReturn(Optional.ofNullable(album));
        albumService.deleteAlbumFromFavourites(20L, albumDto.getId());

        assertAll(
                () -> verifyValidateUserExists(1, 20L),
                () -> verify(albumRepository, times(1)).deleteAlbumFromFavorites(
                        albumDto.getId(), 20L)
        );
    }

    @Test
    void delete_AlbumIsDeleted_IsValid() {
        whenAlbumRepositoryFindById(album.getId());

        albumService.delete(20L, album.getId());

        assertAll(
                () -> verifyAlbumRepositoryFindById(1, album.getId()),
                () -> verifyValidateIfUserIsAuthor(1, 20L),
                () -> verify(albumRepository, times(1)).delete(album)
        );
    }

    private void whenFilterIsApplicable() {
        when(albumFilter.isApplicable(albumFilterDto)).thenReturn(true);
    }

    private void whenAlbumRepositoryFindById(long albumId) {
        when(albumRepository.findById(albumId)).thenReturn(Optional.ofNullable(album));
    }

    private void whenPostRepositoryFindById(long postId) {
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
    }

    private void verifyAlbumRepositoryFindById(int times, long albumId) {
        verify(albumRepository, times(times)).findById(albumId);
    }

    private void verifyPostRepositoryFindById(int times, long postId) {
        verify(postRepository, times(times)).findById(postId);
    }

    private void verifyFilterIsApplicable(int times) {
        verify(albumFilter, times(times)).isApplicable(albumFilterDto);
    }

    private void verifyFilterIsApplied(int times) {
        verify(albumFilter, times(times)).apply(List.of(album), albumFilterDto);
    }

    private void verifyValidateAlbumTitle(int times) {
        verify(albumValidator, times(times)).validateAlbumTitle(albumDto);
    }

    private void verifyValidateUserExists(int times, long userId) {
        verify(userValidator, times(times)).validateUserExist(userId);
    }

    private void verifyValidateIfUserIsAuthor(int times, long userId) {
        verify(albumValidator, times(times)).validateIfUserIsAuthor(userId, album);
    }

    private void verifyValidateUpdatedAlbum(int times, long userId) {
        verify(albumValidator, times(times)).validateUpdatedAlbum(userId, albumDto);
    }
}
