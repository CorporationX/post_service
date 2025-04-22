package faang.school.postservice.servise;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.filter.service.AlbumFilterService;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
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

    @Mock
    private AlbumFilterService albumFilterService;

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
//        doNothing().when(userExistValidator).userExist(anyLong());
//        when(userContext.getUserId()).thenReturn(AUTHOR_ID);

        AlbumDto result = albumService.createAlbum(albumDto);

        assertNotNull(result);
        assertEquals(ALBUM_ID, result.getId());
        assertEquals(ALBUM_TITLE, result.getTitle());
        assertEquals(ALBUM_DESCRIPTION, result.getDescription());

        verify(albumValidator).validateUniqueTitle(any(Album.class));
//        verify(userExistValidator).userExist(AUTHOR_ID);
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

    @Test
    public void test_addAlbumToFavorite() {
        when(albumRepository.findById(ALBUM_ID)).thenReturn(album);

        AlbumDto result = albumService.addAlbumToFavorite(ALBUM_ID, AUTHOR_ID);

        assertNotNull(result);
        assertEquals(ALBUM_ID, result.getId());

        verify(albumValidator).validateAddAlbumToFavorite(album, AUTHOR_ID);
        verify(albumRepository).addAlbumToFavorites(ALBUM_ID, AUTHOR_ID);
        verify(albumRepository).save(album);
    }

    @Test
    public void test_removeAlbumFromFavorite() {
        when(albumRepository.findById(ALBUM_ID)).thenReturn(album);

        AlbumDto result = albumService.removeAlbumFromFavorite(ALBUM_ID, AUTHOR_ID);

        assertNotNull(result);
        assertEquals(ALBUM_ID, result.getId());

        verify(albumValidator).validateRemoveAlbumFromFavorite(album, AUTHOR_ID);
        verify(albumRepository).deleteAlbumFromFavorites(ALBUM_ID, AUTHOR_ID);
        verify(albumRepository).save(album);
    }

    @Test
    public void test_getAlbumById() {
        when(albumRepository.findById(ALBUM_ID)).thenReturn(album);

        AlbumDto result = albumService.getAlbumById(ALBUM_ID);

        assertNotNull(result);
        assertEquals(ALBUM_ID, result.getId());

        verify(albumRepository).findById(ALBUM_ID);
    }

    @Test
    public void test_getAllUserAlbums_withFilters() {
        List<Album> albums = List.of(album, savedAlbum);
        AlbumFilterDto filterDto = new AlbumFilterDto();

        when(albumRepository.findByAuthorId(AUTHOR_ID)).thenReturn(albums.stream());
        when(albumFilterService.applyFilters(any(), eq(filterDto)))
                .thenReturn(albums.stream());

        List<AlbumDto> result = albumService.getAllUserAlbums(AUTHOR_ID, filterDto);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ALBUM_ID, result.get(0).getId());

        verify(albumRepository).findByAuthorId(AUTHOR_ID);
        verify(albumFilterService).applyFilters(any(), eq(filterDto));
    }

    @Test
    public void test_getAllAlbums_withFilters() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setAuthorId(1L);
        album1.setTitle("Album 1");
        album1.setDescription("Description 1");

        Album album2 = new Album();
        album2.setId(2L);
        album2.setAuthorId(1L);
        album2.setTitle("Album 2");
        album2.setDescription("Description 2");

        List<Album> albums = Arrays.asList(album1, album2);

        when(albumRepository.findAll()).thenReturn(albums);
        when(albumFilterService.applyFilters(any(), any())).thenReturn(albums.stream());

        List<AlbumDto> result = albumService.getAllAlbums(new AlbumFilterDto());

        assertEquals(2, result.size());
        assertEquals("Album 1", result.get(0).getTitle());
        assertEquals("Album 2", result.get(1).getTitle());

        verify(albumFilterService, times(1)).applyFilters(any(), any());
    }

    @Test
    void test_getAllUserFavoriteAlbums_withFilters() {
        // given
        long userId = 1L;
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setTitlePattern("t");

        Album album1 = new Album();
        Album album2 = new Album();

        List<Album> favoriteAlbums = List.of(album1, album2);
        Stream<Album> filteredStream = favoriteAlbums.stream(); // допустим, фильтрация ничего не убрала

        when(albumRepository.findFavoriteAlbumsByAuthorId(userId)).thenReturn(favoriteAlbums.stream());
        when(albumFilterService.applyFilters(any(), eq(filterDto))).thenReturn(favoriteAlbums.stream());

        List<AlbumDto> result = albumService.getAllUserFavoriteAlbums(userId, filterDto);

        assertEquals(2, result.size());
        verify(albumRepository).findFavoriteAlbumsByAuthorId(userId);
        verify(albumFilterService).applyFilters(any(), eq(filterDto));
    }

    @Test
    void test_updateAlbum_success() {
        when(albumRepository.findById(ALBUM_ID)).thenReturn(album);
        when(albumRepository.save(album)).thenReturn(album);

        AlbumDto result = albumService.updateAlbum(ALBUM_ID, AUTHOR_ID, albumDto);

        System.out.println("Returned AlbumDto: " + result);

        assertNotNull(result);

        assertEquals(albumDto.getTitle(), result.getTitle());
        assertEquals(albumDto.getDescription(), result.getDescription());
        assertEquals(ALBUM_ID, result.getId());

        verify(albumRepository).findById(ALBUM_ID);
        verify(albumValidator).validateUpdateAlbum(album, AUTHOR_ID, albumDto);
        verify(albumRepository).save(album);
    }

    @Test
    void test_deleteAlbum_success() {
        when(albumRepository.findById(ALBUM_ID)).thenReturn(album);
        doNothing().when(albumValidator).validateDeleteAlbum(album, AUTHOR_ID);
        when(albumMapper.toAlbumDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.deleteAlbum(ALBUM_ID, AUTHOR_ID);

        assertEquals(albumDto, result);
        verify(albumRepository).findById(ALBUM_ID);
        verify(albumValidator).validateDeleteAlbum(album, AUTHOR_ID);
        verify(albumMapper).toAlbumDto(album);
        verify(albumRepository).deleteById(ALBUM_ID);
    }
}
