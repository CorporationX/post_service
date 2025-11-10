package faang.school.postservice.albumService;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.AlbumServiceImpl;
import faang.school.postservice.validate.AlbumValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AlbumServiceImplTests {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserContext userContext;
    @Spy
    private final AlbumMapper albumMapper = Mappers.getMapper(AlbumMapper.class);
    @Mock
    private AlbumValidator albumValidator;

    @InjectMocks
    private AlbumServiceImpl albumServiceImpl;

    private Album album;
    private AlbumDto albumDto;
    private Post post;
    private UserDto userDto;
    private List<Album> albums;

    @BeforeEach
    void setUp() {

        album = new Album();
        album.setVisibility(AlbumVisibility.PUBLIC);
        albumDto = new AlbumDto();
        albumDto.setId(1L);
        albumDto.setVisibility(AlbumVisibility.PUBLIC);
        post = new Post();
        post.setId(1L);
        userDto = new UserDto(1L, "v", "@");

        albumServiceImpl = new AlbumServiceImpl(
            albumRepository,
            albumMapper,
            userContext,
            albumValidator
        );
    }

    @Test
    void testGetAlbum() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumRepository.findById(anyLong())).thenReturn(Optional.ofNullable(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumValidator.hasPermission(any(Album.class))).thenReturn(true);
        AlbumDto result = albumServiceImpl.getAlbum(1L);

        verify(albumRepository, times(1)).findById(anyLong());
        assertEquals(albumDto.getTitle(), result.getTitle());
    }

    @Test
    void testGetAlbumEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> albumServiceImpl.getAlbum(1L));
    }

    @Test
    void testGetAllAlbums() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumRepository.findAll()).thenReturn(List.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumValidator.hasPermission(any(Album.class))).thenReturn(true);
        List<AlbumDto> result = albumServiceImpl.getAllAlbums();

        verify(albumRepository, times(1)).findAll();
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getDescription(), result.get(0).getDescription());
    }

    @Test
    void testUpdate() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumValidator.hasPermission(any(Album.class))).thenReturn(true);
        AlbumDto result = albumServiceImpl.update(albumDto);

        verify(albumRepository, times(1)).save(any(Album.class));
        assertEquals(albumDto.getTitle(), result.getTitle());
        assertEquals(albumDto.getDescription(), result.getDescription());
    }

    @Test
    void updateVisibilityPublic() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumValidator.hasPermission(any(Album.class))).thenReturn(true);
        AlbumDto result = albumServiceImpl.updateVisibility(1L, AlbumVisibility.PUBLIC, null);

        verify(albumRepository, times(1)).save(any(Album.class));
        assertEquals(AlbumVisibility.PUBLIC, result.getVisibility());
    }

    @Test
    void updateVisibilityPrivate() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        Album album = new Album();
        album.setVisibility(AlbumVisibility.PRIVATE);

        AlbumDto albumDto = new AlbumDto();
        albumDto.setVisibility(AlbumVisibility.PRIVATE);

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumValidator.hasPermission(any(Album.class))).thenReturn(true);
        AlbumDto result = albumServiceImpl.updateVisibility(1L, AlbumVisibility.PRIVATE, null);

        verify(albumRepository, times(1)).save(any(Album.class));
        assertEquals(AlbumVisibility.PRIVATE, result.getVisibility());
    }

    @Test
    void updateVisibilitySelectedUser() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        Album album = new Album();
        album.setVisibility(AlbumVisibility.SELECTED_USERS);
        album.setFavouriteUserIds(List.of(1L, 2L));

        AlbumDto albumDto = new AlbumDto();
        albumDto.setVisibility(AlbumVisibility.SELECTED_USERS);
        albumDto.setFavouriteUserIds(List.of(1L, 2L));

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumValidator.hasPermission(any(Album.class))).thenReturn(true);
        AlbumDto result = albumServiceImpl.updateVisibility(1L, AlbumVisibility.SELECTED_USERS, null);

        verify(albumRepository, times(1)).save(any(Album.class));
        assertEquals(AlbumVisibility.SELECTED_USERS, result.getVisibility());
        assertEquals(List.of(1L, 2L), result.getFavouriteUserIds());
    }

    @Test
    void updateVisibilitySubscribers() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        Album album = new Album();
        album.setVisibility(AlbumVisibility.SUBSCRIBERS);

        AlbumDto albumDto = new AlbumDto();
        albumDto.setVisibility(AlbumVisibility.SUBSCRIBERS);

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(albumValidator.hasPermission(any(Album.class))).thenReturn(true);
        AlbumDto result = albumServiceImpl.updateVisibility(1L, AlbumVisibility.SUBSCRIBERS, null);

        verify(albumRepository, times(1)).save(any(Album.class));
        assertEquals(AlbumVisibility.SUBSCRIBERS, result.getVisibility());
    }

    private void prepareDtoWithTitleAndDescription() {
        albumDto.setTitle("Title");
        albumDto.setDescription("Description");
    }

    private void prepareAlbumEntity() {
        album.setId(1L);
        album.setTitle("Title");
        album.setAuthorId(1L);
        album.setPosts(new ArrayList<>());
    }
}