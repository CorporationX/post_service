package faang.school.postservice.servise;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.dto.posts.PostDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.mapper.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepositoryAdapter;
import faang.school.postservice.service.impl.AlbumServiceImpl;
import faang.school.postservice.validator.AlbumValidator;
import faang.school.postservice.validator.UserExistValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceImplTest {

    @InjectMocks
    private AlbumServiceImpl albumService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;

    @Mock
    private AlbumValidator albumValidator;

    @Mock
    private UserExistValidator userExistValidator;

    @Mock
    private UserContext userContext;

    @Spy
    private AlbumMapper albumMapper = new AlbumMapperImpl();

    private static final long AUTHOR_ID = 1L;
    private static final long ALBUM_ID = 1L;
    private static final long POST_ID = 100L;
    private static final String ALBUM_TITLE = "Test Album";
    private static final String ALBUM_DESCRIPTION = "Test Description";
    private static final String ALBUM_DTO_TITLE = "Test AlbumDto";
    private static final String ALBUM_DTO_DESCRIPTION = "Test DescriptionDto";

    private Album album;
    private Album savedAlbum;
    private AlbumDto albumDto;

    @BeforeEach
    void setUp() {
        album = new Album();
        album.setId(ALBUM_ID);
        album.setAuthorId(AUTHOR_ID);
        album.setTitle(ALBUM_TITLE);
        album.setDescription(ALBUM_DESCRIPTION);

        savedAlbum = new Album();
        savedAlbum.setId(ALBUM_ID);
        savedAlbum.setAuthorId(AUTHOR_ID);
        savedAlbum.setTitle(ALBUM_TITLE);
        savedAlbum.setDescription(ALBUM_DESCRIPTION);

        albumDto = new AlbumDto();
        albumDto.setId(ALBUM_ID);
        albumDto.setTitle(ALBUM_DTO_TITLE);
        albumDto.setDescription(ALBUM_DTO_DESCRIPTION);
    }

    @Test
    public void test_createAlbum() {
        Album album = new Album();
        album.setAuthorId(AUTHOR_ID);
        album.setId(ALBUM_ID);

        when(albumRepository.save(any(Album.class))).thenReturn(savedAlbum);
        doNothing().when(albumValidator).validateUniqueTitle(any(Album.class));
        doNothing().when(userExistValidator).userExist(anyLong());
        when(userContext.getUserId()).thenReturn(AUTHOR_ID);

        AlbumDto result = albumService.createAlbum(albumDto);

        assertNotNull(result);
        assertEquals(ALBUM_ID, result.getId());
        assertEquals(ALBUM_TITLE, result.getTitle());
        assertEquals(ALBUM_DESCRIPTION, result.getDescription());

        verify(albumValidator).validateUniqueTitle(any(Album.class));
        verify(userExistValidator).userExist(AUTHOR_ID);
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    public void test_addPostToAlbum() {
        Album album = new Album();
        album.setId(ALBUM_ID);
        album.setAuthorId(AUTHOR_ID);
        album.setPosts(new ArrayList<>());

        Post post = new Post();
        post.setId(POST_ID);

        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(ALBUM_ID);

        when(albumRepository.findById(ALBUM_ID)).thenReturn(album);
        when(postRepositoryAdapter.findById(POST_ID)).thenReturn(post);
        doNothing().when(albumValidator).validateAddPostToAlbum(album, POST_ID, AUTHOR_ID);
        when(albumMapper.toAlbumDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.addPostToAlbum(ALBUM_ID, POST_ID, AUTHOR_ID);

        assertNotNull(result);
        assertEquals(ALBUM_ID, result.getId());
        assertTrue(album.getPosts().contains(post));

        verify(albumRepository).findById(ALBUM_ID);
        verify(postRepositoryAdapter).findById(POST_ID);
        verify(albumValidator).validateAddPostToAlbum(album, POST_ID, AUTHOR_ID);
        verify(albumRepository).save(album);
        verify(albumMapper).toAlbumDto(album);
    }

    @Test
    public void test_removePostFromAlbum() {
        Album album = new Album();
        album.setId(ALBUM_ID);
        album.setAuthorId(AUTHOR_ID);

        Post post = new Post();
        post.setId(POST_ID);

        album.setPosts(new ArrayList<>(List.of(post)));

        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(ALBUM_ID);

        when(albumRepository.findById(ALBUM_ID)).thenReturn(album);
        doNothing().when(albumValidator).validateRemovePostFromAlbum(album, POST_ID, AUTHOR_ID);
        when(albumMapper.toAlbumDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.removePostFromAlbum(ALBUM_ID, POST_ID, AUTHOR_ID);

        assertNotNull(result);
        assertEquals(ALBUM_ID, result.getId());
        assertFalse(album.getPosts().contains(post));

        verify(albumRepository).findById(ALBUM_ID);
        verify(albumValidator).validateRemovePostFromAlbum(album, POST_ID, AUTHOR_ID);
        verify(albumRepository).save(album);
        verify(albumMapper).toAlbumDto(album);
    }
}
