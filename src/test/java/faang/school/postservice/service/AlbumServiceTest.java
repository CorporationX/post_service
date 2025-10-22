package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.filters.AlbumFilterDto;
import faang.school.postservice.mapper.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.FavoriteAlbums;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.FavoriteAlbumsRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    AlbumRepository albumRepository;

    @Mock
    UserContext userContext;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    PostRepository postRepository;

    @Mock
    FavoriteAlbumsRepository favoriteAlbumsRepository;

    @Spy
    AlbumMapperImpl albumMapper;

    @InjectMocks
    AlbumServiceImpl albumService;

    @Captor
    private ArgumentCaptor<Album> albumCaptor;

    @Captor
    private ArgumentCaptor<FavoriteAlbums> favoriteAlbumCaptor;

    TestAlbumTitleFilter testAlbumTitleFilter = new TestAlbumTitleFilter();
    TestAlbumDateFilter testAlbumDateFilter = new TestAlbumDateFilter();

    AlbumDto albumDto;

    @BeforeEach
    public void setUp() {
        albumDto = new AlbumDto(1L,
                null,
                null,
                2L,
                null);
    }


    @Test
    public void testCreateUnregisteredUserAlbum() {
        when(userServiceClient.getUser(anyLong())).thenThrow(new RuntimeException());

        assertThrows(DataValidationException.class,
                () -> albumService.createAlbum(albumDto, 1L));
    }

    @Test
    public void testCreateExistingUsersAlbum() {
        when(userServiceClient.getUser(anyLong())).thenReturn(null);
        when(albumRepository.existsByTitleAndAuthorId(any(), anyLong())).thenReturn(true);

        assertThrows(ForbiddenException.class,
                () -> albumService.createAlbum(albumDto, 1L));
    }

    @Test
    public void testCreateAlbum() {
        when(userServiceClient.getUser(anyLong())).thenReturn(null);
        when(albumRepository.existsByTitleAndAuthorId(any(), anyLong())).thenReturn(false);
        when(albumRepository.save(any())).thenReturn(new Album());

        albumService.createAlbum(albumDto, 1L);

        verify(albumRepository).save(albumCaptor.capture());
        Album album = albumCaptor.getValue();
        assertEquals(2L, album.getAuthorId());
    }

    @Test
    public void testUpdateNotExistingAlbum() {
        when(albumRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> albumService.updateAlbum(1L, albumDto));
    }

    @Test
    public void testUpdateAlbum() {
        when(albumRepository.existsById(anyLong())).thenReturn(true);
        when(albumRepository.save(any())).thenReturn(new Album());
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(new Album()));

        albumService.updateAlbum(1L, albumDto);

        verify(albumRepository).save(albumCaptor.capture());
        Album albumCaught = albumCaptor.getValue();
        assertEquals(2L, albumCaught.getAuthorId());
    }

    @Test
    public void testDeleteNotExistingAlbum() {
        when(albumRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> albumService.deleteAlbum(1L));
    }

    @Test
    public void testDeleteAlbum() {
        when(albumRepository.existsById(anyLong())).thenReturn(true);

        albumService.deleteAlbum(1L);

        verify(albumRepository).deleteById(anyLong());
    }


    @Test
    public void testGetByNotExistingId() {
        when(albumRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> albumService.getById(1L));
    }

    @Test
    public void testGetById() {
        when(albumRepository.existsById(anyLong())).thenReturn(true);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(new Album()));

        albumService.getById(1L);

        verify(albumRepository).findById(anyLong());
    }

    @Test
    public void testGetAll() {
        when(albumRepository.findAll()).thenReturn(null);

        albumService.getAllAlbums();

        verify(albumRepository).findAll();
    }

    @Test
    public void testGetFilteredByTitleAlbums() {
        AlbumServiceImpl albumService = prepareService();
        List<Album> albums = prepareAlbums();
        AlbumFilterDto filteredAlbumsDto = prepareFilteredDto();

        when(albumRepository.findAll()).thenReturn(albums);

        List<AlbumDto> list = albumService.getAlbums(filteredAlbumsDto);

        assertEquals(1, list.size());
        assertEquals("album1", list.get(0).title());

    }

    @Test
    public void testGetFilteredAuthorsAlbums() {
        AlbumServiceImpl albumService = prepareService();
        List<Album> albums = prepareAlbums();
        AlbumFilterDto filteredAlbumsDto = prepareFilteredDto();

        when(albumRepository.findByAuthorId(anyLong())).thenReturn(albums);

        List<AlbumDto> list = albumService.getAuthorsAlbums(1L, filteredAlbumsDto);

        assertEquals(1, list.size());
        assertEquals("album1", list.get(0).title());
    }

    @Test
    public void testAddPosts() {

    }

    @Test
    public void testAddExistingToFavoriteAlbums() {
    when(favoriteAlbumsRepository.existsByAlbumIdAndUserId(anyLong(), anyLong()))
            .thenReturn(true);

    assertThrows(ForbiddenException.class,
            () -> albumService.addToFavoriteAlbums(1L, 1L));
    }

    @Test
    public void testAddToFavoriteAlbums() {
        when(favoriteAlbumsRepository.existsByAlbumIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);

        albumService.addToFavoriteAlbums(3L, 5L);
        verify(favoriteAlbumsRepository).save(favoriteAlbumCaptor.capture());

        FavoriteAlbums favoriteAlbum = favoriteAlbumCaptor.getValue();
        assertEquals(3L, favoriteAlbum.getAlbumId());
        assertEquals(5L, favoriteAlbum.getUserId());
    }

    @Test
    public void testRemoveNotExistingFavorite() {
        when(favoriteAlbumsRepository.existsByAlbumIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);

        assertThrows(ForbiddenException.class,
                () -> albumService.removeFromFavoriteAlbum(1L, 1L));

    }

    @Test
    public void testRemoveFromFavoriteAlbums() {
        when(favoriteAlbumsRepository.existsByAlbumIdAndUserId(anyLong(), anyLong()))
                .thenReturn(true);

        albumService.removeFromFavoriteAlbum(1L, 1L);

        verify(favoriteAlbumsRepository).deleteByAlbumIdAndUserId(anyLong(), anyLong());
    }

    private AlbumServiceImpl prepareService() {
        AlbumServiceImpl service= new AlbumServiceImpl(
                albumRepository,
                albumMapper,
                postRepository,
                List.of(testAlbumTitleFilter, testAlbumDateFilter),
                favoriteAlbumsRepository,
                userServiceClient
        );

        return service;
    }

    private List<Album> prepareAlbums() {
        Album album1 = Album.builder()
                .title("album1")
                .createdAt(LocalDateTime.now())
                .build();
        Album album2 = Album.builder()
                .title("album2")
                .createdAt(LocalDateTime.now())
                .build();
        return List.of(album1, album2);
    }

    private AlbumFilterDto prepareFilteredDto() {

        return new AlbumFilterDto(
                "album1",
                null,
                null
        );
    }

}
