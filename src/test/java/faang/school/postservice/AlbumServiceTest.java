package faang.school.postservice;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.dto.album.AlbumDto;
import faang.school.postservice.model.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.AlbumService;
import faang.school.postservice.validator.AlbumValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private AlbumValidator validator;

    @InjectMocks
    private AlbumService albumService;

    private Album album;
    private AlbumDto albumDto;
    private AlbumFilterDto albumFilterDto;

    @BeforeEach
    void setUp() {
        album = new Album();
        album.setId(1L);
        album.setTitle("Test Album");
        album.setDescription("Test Description");

        albumDto = new AlbumDto();
        albumDto.setTitle("Test Album");
        albumDto.setDescription("Test Description");

        albumFilterDto = new AlbumFilterDto();
        albumFilterDto.setTitle("Test Album");
        albumFilterDto.setAuthorId(1L);
    }

    @Test
    public void testCreateAlbum() {
        when(userContext.getUserId()).thenReturn(1L);
        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.albumToAlbumDto(album)).thenReturn(albumDto);

        doNothing().when(validator).validateUser(anyLong());
        doNothing().when(validator).validateAlbumNotExists(anyString(), anyLong());

        AlbumDto createdAlbum = albumService.create(albumDto);

        verify(validator, times(1)).validateAlbumNotExists(anyString(), anyLong());
        verify(albumRepository, times(1)).save(any(Album.class));

        assertEquals("Test Album", createdAlbum.getTitle());
        assertEquals("Test Description", createdAlbum.getDescription());
    }

    @Test
    public void testGetAlbumById() {
        when(userContext.getUserId()).thenReturn(1L);
        when(albumMapper.albumToAlbumDto(album)).thenReturn(albumDto);
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        doNothing().when(validator).validateUser(anyLong());

        AlbumDto foundAlbum = albumService.getAlbumById(1L);

        verify(albumRepository, times(1)).findById(1L);

        assertNotNull(foundAlbum);
        assertEquals("Test Album", foundAlbum.getTitle());
        assertEquals("Test Description", foundAlbum.getDescription());
    }

    @Test
    public void testAddPostToAlbum() {
        Post post = new Post();
        post.setId(1L);

        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(validator).validateUser(anyLong());

        when(postRepository.existsInAlbum(anyLong(), anyLong())).thenReturn(false);
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        String result = albumService.addPostToAlbum(1L, 1L);

        verify(albumRepository, times(1)).save(any(Album.class));

        assertEquals("Post id = 1 added in album id = 1, title = Test Album", result);
    }

    @Test
    public void testDeletePostFromAlbum() {
        Post post = new Post();
        post.setId(1L);

        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(validator).validateUser(anyLong());
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(albumMapper.albumToAlbumDto(album)).thenReturn(albumDto);
        when(albumRepository.save(album)).thenReturn(album);

        AlbumDto updatedAlbum = albumService.deletePostFromAlbum(1L, 1L);

        verify(albumRepository, times(1)).save(any(Album.class));

        assertNotNull(updatedAlbum);
        assertEquals("Test Album", updatedAlbum.getTitle());
    }

    @Test
    public void testAddAlbumToFavorites() {
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(validator).validateUser(anyLong());
        when(albumRepository.existsInFavorites(1L, 1L)).thenReturn(false);
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        String result = albumService.addAlbumToFavorites(1L);

        verify(albumRepository, times(1)).addAlbumToFavorites(1L, 1L);

        assertEquals("Album with id = 1, title = Test Album added to favorites", result);
    }

    @Test
    public void testDeleteAlbumFromFavorites() {
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(validator).validateUser(anyLong());
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        String result = albumService.deleteAlbumFromFavorites(1L);

        verify(albumRepository, times(1)).deleteAlbumFromFavorites(1L, 1L);

        assertEquals("Album with id = 1, title = Test Album deleted from favorites", result);
    }

    @Test
    public void testDeleteAlbum() {
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(validator).validateUser(anyLong());
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        doNothing().when(validator).validateUserIsAuthor(anyLong(), anyLong());

        albumService.delete(1L);

        verify(albumRepository, times(1)).delete(any(Album.class));
    }

    @Test
    public void testCreateAlbum_AlreadyExists() {
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(validator).validateUser(anyLong());
        doThrow(new DataValidationException("Album with this title already exists"))
                .when(validator).validateAlbumNotExists(anyString(), anyLong());

        AlbumDto albumDto = new AlbumDto();
        albumDto.setTitle("Test Album");
        albumDto.setDescription("Test Description");

        DataValidationException thrown = assertThrows(DataValidationException.class, () -> {
            albumService.create(albumDto);
        });

        assertEquals("Album with this title already exists", thrown.getMessage());
    }

    @Test
    public void testAddPostToAlbum_AlreadyExists() {
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(validator).validateUser(anyLong());
        when(postRepository.existsInAlbum(anyLong(), anyLong())).thenReturn(true);

        String result = albumService.addPostToAlbum(1L, 1L);

        assertEquals("Post id = 1 already exists in album id = 1", result);
    }

    @Test
    public void testDeleteAlbum_NotOwner() {
        Album album = new Album();
        album.setId(1L);
        album.setAuthorId(2L);

        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(validator).validateUser(anyLong());
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        doThrow(new DataValidationException("The user cannot modify someone else's album"))
                .when(validator).validateUserIsAuthor(1L, 2L);

        DataValidationException thrown = assertThrows(DataValidationException.class, () -> {
            albumService.delete(1L);
        });

        assertEquals("The user cannot modify someone else's album", thrown.getMessage());
    }

    @Test
    public void testUpdateAlbum_Success() {
        albumDto = new AlbumDto();
        albumDto.setTitle("Updated Title");
        albumDto.setDescription("Updated Description");
        albumDto.setAuthorId(1L);
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(validator).validateUser(anyLong());
        doNothing().when(validator).validateUserIsAuthor(1L, 1L);

        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.albumToAlbumDto(any(Album.class))).thenReturn(albumDto);

        AlbumDto result = albumService.update(1L, albumDto);

        verify(validator).validateUser(1L);
        verify(validator).validateUserIsAuthor(1L, 1L);
        verify(albumRepository).findById(1L);
        verify(albumRepository).save(any(Album.class));
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
    }

    @Test
    public void testUpdateAlbum_AlbumNotFound() {
        albumDto = new AlbumDto();
        albumDto.setTitle("Updated Title");
        albumDto.setDescription("Updated Description");
        albumDto.setAuthorId(1L);

        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(validator).validateUser(anyLong());
        doNothing().when(validator).validateUserIsAuthor(1L, 1L);

        when(albumRepository.findById(anyLong())).thenReturn(Optional.empty());

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            albumService.update(1L, albumDto);
        });

        assertEquals("Album with id = 1 not found", thrown.getMessage());
        verify(albumRepository).findById(1L);
        verify(albumRepository, never()).save(any());
    }

    @Test
    public void testUpdateAlbum_NotAuthor() {
        albumDto = new AlbumDto();
        albumDto.setTitle("Updated Title");
        albumDto.setDescription("Updated Description");
        albumDto.setAuthorId(1L);

        when(userContext.getUserId()).thenReturn(2L);  // User is not the author
        doNothing().when(validator).validateUser(anyLong());
        doThrow(new DataValidationException("The user cannot modify someone else's album"))
                .when(validator).validateUserIsAuthor(2L, 1L);

        DataValidationException thrown = assertThrows(DataValidationException.class, () -> {
            albumService.update(1L, albumDto);
        });

        assertEquals("The user cannot modify someone else's album", thrown.getMessage());
        verify(validator).validateUser(2L);
        verify(validator).validateUserIsAuthor(2L, 1L);
        verify(albumRepository, never()).findById(1L);
    }
}

