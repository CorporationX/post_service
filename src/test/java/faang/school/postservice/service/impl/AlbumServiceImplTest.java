package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceImplTest {
    private final Long albumId = 2L;
    private final Long postId = 3L;
    private final Long userId = 4L;

    private AlbumDto albumDto;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AlbumMapper albumMapper;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @BeforeEach
    public void setUp() {
        albumDto = new AlbumDto();
        albumDto.setAuthorId(1L);
        albumDto.setTitle("example title");
    }

    @Test
    public void testCreateAlbumByNonExistingUser() {
        when(userServiceClient.existsUserById(albumDto.getAuthorId())).thenReturn(false);

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> albumService.createAlbum(albumDto)
        );
        Assertions.assertEquals("User not found", thrown.getMessage());
        verify(albumRepository, never()).save(any(Album.class));
    }

    @Test
    public void testCreateAlbumWithExistingTitleForUser() {
        when(userServiceClient.existsUserById(albumDto.getAuthorId())).thenReturn(true);
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())).thenReturn(true);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> albumService.createAlbum(albumDto)
        );
        Assertions.assertEquals("Album with title " + albumDto.getTitle() + " already exists", thrown.getMessage());
        verify(albumRepository, never()).save(any(Album.class));
    }

    @Test
    public void testCreateAlbumSuccessfully() {
        when(userServiceClient.existsUserById(albumDto.getAuthorId())).thenReturn(true);
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())).thenReturn(false);
        Album album = new Album();
        when(albumMapper.toEntity(albumDto)).thenReturn(album);

        albumService.createAlbum(albumDto);

        verify(albumRepository, times(1)).save(album);
    }

    @Test
    public void testUpdateAlbumWithExistingTitleForUser() {
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())).thenReturn(true);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> albumService.updateAlbum(albumId, albumDto)
        );
        Assertions.assertEquals("Album with title " + albumDto.getTitle() + " already exists", thrown.getMessage());
        verify(albumRepository, never()).save(any(Album.class));
    }

    @Test
    public void testUpdateNonExistingAlbum() {
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())).thenReturn(false);
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> albumService.updateAlbum(albumId, albumDto)
        );

        Assertions.assertEquals("Album not found", thrown.getMessage());
        verify(albumRepository, never()).save(any(Album.class));
    }

    @Test
    public void testUpdateAlbumSuccessfully() {
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())).thenReturn(false);
        Album album = new Album();
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.updateAlbum(albumId, albumDto);

        verify(albumRepository, times(1)).save(album);
    }

    @Test
    public void testAddPostToNoExistingAlbum() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> albumService.addPostToAlbum(albumId, postId)
        );

        Assertions.assertEquals("Album not found", thrown.getMessage());
        verify(albumRepository, never()).save(any(Album.class));
    }

    @Test
    public void testAddNotExistingPostToAlbum() {
        Album album = new Album();
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> albumService.addPostToAlbum(albumId, postId)
        );

        Assertions.assertEquals("Post not found", thrown.getMessage());
        verify(albumRepository, never()).save(any(Album.class));
    }

    @Test
    public void testAddPostToAlbumSuccessfully() {
        Album album = new Album();
        album.setPosts(new ArrayList<>());
        Post post = new Post();
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        albumService.addPostToAlbum(albumId, postId);

        assertTrue(album.getPosts().contains(post));
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    public void testGetNoExistingAlbumById() {
        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> albumService.getAlbumById(albumId, userId)
        );

        Assertions.assertEquals("Album not found", thrown.getMessage());
        verify(albumMapper, never()).toDto(any(Album.class));
    }

    @Test
    public void testGetNotVisibleAlbumById() {
        Album album = new Album();
        album.setId(albumId);
        album.setVisibility(AlbumVisibility.SELECTED_USERS);
        Post  post = new Post();
        post.setId(postId);

        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.of(album));
        album.setPosts(List.of(post));

        when(albumRepository.getSelectedUserIdsForAlbum(album.getId())).thenReturn(Collections.emptyList());

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> albumService.getAlbumById(albumId, userId)
        );

        Assertions.assertEquals("Album is not visible for user", thrown.getMessage());
        verify(albumMapper, never()).toDto(any(Album.class));
    }

    @Test
    public void testGetAlbumByIdSuccessfully() {
        Album album = new Album();
        album.setId(albumId);
        album.setVisibility(AlbumVisibility.SELECTED_USERS);
        Post  post = new Post();
        post.setId(postId);

        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.of(album));
        album.setPosts(List.of(post));

        when(albumRepository.getSelectedUserIdsForAlbum(album.getId())).thenReturn(List.of(userId));

        when(albumMapper.toDto(album)).thenReturn(albumDto);
        albumDto.setPostIds(List.of(postId));

        Assertions.assertEquals(albumDto, albumService.getAlbumById(albumId, userId));
    }
}
