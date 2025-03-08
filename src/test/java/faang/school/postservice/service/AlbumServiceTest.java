package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDTO;
import faang.school.postservice.exception.BadRequestException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.FavoriteAlbum;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.adapter.AlbumRepositoryAdapter;
import faang.school.postservice.repository.adapter.FavoriteAlbumRepositoryAdapter;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    @Mock
    private AlbumRepositoryAdapter albumRepositoryAdapter;
    @Mock
    private AlbumMapper albumMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;
    @Mock
    private FavoriteAlbumRepositoryAdapter favoriteAlbumRepositoryAdapter;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private AlbumService albumService;

    private Long userId;
    private Long albumId;
    private Long postId;
    private Album defaultAlbum;
    private AlbumDTO defaultAlbumDTO;

    @BeforeEach
    void setUp() {
        userId = 1L;
        albumId = 100L;
        postId = 200L;

        defaultAlbum = new Album();
        defaultAlbum.setAuthorId(userId);
        defaultAlbum.setTitle("Default Album");

        defaultAlbumDTO = new AlbumDTO();
        defaultAlbumDTO.setAuthorId(userId);
        defaultAlbumDTO.setTitle("Default Album");
    }

    @Test
    void save_success() {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("Test Album");
        albumDTO.setAuthorId(userId);

        Album albumEntity = new Album();
        albumEntity.setTitle("Test Album");
        albumEntity.setAuthorId(userId);

        Album savedAlbumEntity = new Album();
        savedAlbumEntity.setTitle("Test Album");
        savedAlbumEntity.setAuthorId(userId);

        AlbumDTO savedAlbumDTO = new AlbumDTO();
        savedAlbumDTO.setTitle("Test Album");
        savedAlbumDTO.setAuthorId(userId);

        when(albumRepositoryAdapter.existsByTitleAndAuthorId(albumDTO.getTitle(), userId)).thenReturn(false);
        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(albumMapper.toEntity(albumDTO)).thenReturn(albumEntity);
        when(albumRepositoryAdapter.save(albumEntity)).thenReturn(savedAlbumEntity);
        when(albumMapper.toDto(savedAlbumEntity)).thenReturn(savedAlbumDTO);

        AlbumDTO result = albumService.save(albumDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Test Album", result.getTitle());
    }

    @Test
    void save_failedAlbumAlreadyExists() {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("Test Album");
        albumDTO.setAuthorId(userId);

        when(albumRepositoryAdapter.existsByTitleAndAuthorId(albumDTO.getTitle(), userId)).thenReturn(true);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () -> albumService.save(albumDTO));
        Assertions.assertEquals("An album with this title already exists", ex.getMessage());
    }

    @Test
    void save_failedUserNotFound() {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("Test Album");
        albumDTO.setAuthorId(userId);

        when(albumRepositoryAdapter.existsByTitleAndAuthorId(albumDTO.getTitle(), userId)).thenReturn(false);
        when(userServiceClient.getUser(userId)).thenThrow(FeignException.NotFound.class);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> albumService.save(albumDTO));
        Assertions.assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void addPostToAlbum_success() {
        Post post = new Post();
        Album album = new Album();
        album.setAuthorId(userId);

        when(postRepositoryAdapter.getById(postId)).thenReturn(post);
        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);
        when(userContext.getUserId()).thenReturn(userId);

        albumService.addPostToAlbum(albumId, postId);
    }

    @Test
    void addPostToAlbum_failedNotOwner() {
        Post post = new Post();
        Album album = new Album();
        album.setAuthorId(999L);

        when(postRepositoryAdapter.getById(postId)).thenReturn(post);
        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);
        when(userContext.getUserId()).thenReturn(userId);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class,
                () -> albumService.addPostToAlbum(albumId, postId));
        Assertions.assertEquals("Unable to add post to album", ex.getMessage());
    }

    @Test
    void deletePostToAlbum_success() {
        Album album = Mockito.spy(new Album());
        album.setAuthorId(userId);

        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);
        when(userContext.getUserId()).thenReturn(userId);
        albumService.deletePostToAlbum(albumId, postId);

        Mockito.verify(album, Mockito.times(1)).removePost(postId);
    }

    @Test
    void deletePostToAlbum_failedNotOwner() {
        Album album = new Album();
        album.setAuthorId(999L);

        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);
        when(userContext.getUserId()).thenReturn(userId);
        BadRequestException ex = Assertions.assertThrows(BadRequestException.class,
                () -> albumService.deletePostToAlbum(albumId, postId));
        Assertions.assertEquals("Unable to delete post from album", ex.getMessage());
    }

    @Test
    void addAlbumToFavourite_success() {
        Album album = new Album();
        album.setAuthorId(userId);

        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);
        when(favoriteAlbumRepositoryAdapter.existsByAlbumAndAuthorId(album, userId)).thenReturn(false);
        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(userContext.getUserId()).thenReturn(userId);

        albumService.addAlbumToFavourite(albumId);
        Mockito.verify(favoriteAlbumRepositoryAdapter, Mockito.times(1))
                .save(Mockito.any(FavoriteAlbum.class));
    }

    @Test
    void addAlbumToFavourite_failedAlreadyFavourite() {
        Album album = new Album();
        album.setAuthorId(userId);

        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);
        when(favoriteAlbumRepositoryAdapter.existsByAlbumAndAuthorId(album, userId)).thenReturn(true);
        when(userContext.getUserId()).thenReturn(userId);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class,
                () -> albumService.addAlbumToFavourite(albumId));
        Assertions.assertEquals("Album already added to favourites", ex.getMessage());
    }

    @Test
    void deleteAlbumToFavourite_success() {
        Album album = new Album();
        album.setAuthorId(userId);

        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);
        when(userContext.getUserId()).thenReturn(userId);

        albumService.deleteByAlbumIdAndUserId(albumId);
        Mockito.verify(favoriteAlbumRepositoryAdapter, Mockito.times(1)).deleteByAlbumAndUserId(album, userId);
    }

    @Test
    void getById_success() {
        Album album = new Album();
        album.setTitle("Test Album");

        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("Test Album");

        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);
        when(albumMapper.toDto(album)).thenReturn(albumDTO);

        AlbumDTO result = albumService.getById(albumId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Test Album", result.getTitle());
    }

    @Test
    void getAll_success() {
        Album album = new Album();
        album.setTitle("Test Album");
        List<Album> albums = List.of(album);

        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("Test Album");

        when(albumRepositoryAdapter.findAll(Mockito.any())).thenReturn(albums);
        when(albumMapper.toDtoList(albums)).thenReturn(List.of(albumDTO));

        List<AlbumDTO> result = albumService.getAll("Test", LocalDate.now(), userId);
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void update_success() {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("Updated Album");
        albumDTO.setAuthorId(userId);

        Album album = Mockito.spy(new Album());
        album.setAuthorId(userId);

        AlbumDTO updatedAlbumDTO = new AlbumDTO();
        updatedAlbumDTO.setTitle("Updated Album");

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);
        when(albumRepositoryAdapter.existsByTitleAndAuthorId(albumDTO.getTitle(), albumDTO.getAuthorId()))
                .thenReturn(false);
        Mockito.doAnswer(invocation -> {
            album.setTitle(albumDTO.getTitle());
            return null;
        }).when(albumMapper).update(albumDTO, album);
        when(albumMapper.toDto(album)).thenReturn(updatedAlbumDTO);

        AlbumDTO result = albumService.update(albumDTO, albumId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Album", result.getTitle());
    }

    @Test
    void update_failedAlbumAlreadyExists() {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("Updated Album");
        albumDTO.setAuthorId(userId);

        Album album = new Album();
        album.setAuthorId(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);
        when(albumRepositoryAdapter.existsByTitleAndAuthorId(albumDTO.getTitle(), albumDTO.getAuthorId()))
                .thenReturn(true);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class,
                () -> albumService.update(albumDTO, albumId));
        Assertions.assertEquals("An album with this title already exists", ex.getMessage());
    }

    @Test
    void delete_success() {
        Album album = new Album();
        album.setAuthorId(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);

        albumService.delete(albumId);
        Mockito.verify(albumRepositoryAdapter, Mockito.times(1)).delete(album);
    }

    @Test
    void delete_failedNotOwner() {
        Album album = new Album();
        album.setAuthorId(999L);

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepositoryAdapter.getById(albumId)).thenReturn(album);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class,
                () -> albumService.delete(albumId));
        Assertions.assertEquals("Unable to delete album", ex.getMessage());
    }
}
