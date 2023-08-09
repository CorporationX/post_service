package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.album_filter.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Mock
    private List<AlbumFilter> albumFilters;
    @InjectMocks
    private AlbumService albumService;

    @Test
    void createAlbum() {
//        AlbumDto albumDto = AlbumDto.builder().id(1L).build();
//        UserDto user = userServiceClient.getUser(1L);
//        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder().description("description").title("title").authorId(1L).build();
//        when(userServiceClient.getUser(albumCreateDto.getAuthorId())).thenReturn(user);
//        when(albumRepository.existsByTitleAndAuthorId(anyString(), anyLong())).thenReturn(true);
//        albumService.createAlbum(albumCreateDto);
//        assertEquals(1L, albumDto.getId());
    }

    @Test
    void addPostToAlbum() {
    }

    @Test
    void deletePostFromAlbum() {
    }

    @Test
    void addAlbumToFavorites() {
    }

    @Test
    void deleteAlbumFromFavorites() {
    }

    @Test
    void findByIdWithPosts() {
    }

    @Test
    void findAListOfAllYourAlbums() {
    }

    @Test
    void findListOfAllAlbumsInTheSystem() {
    }

    @Test
    void findAListOfAllYourFavoriteAlbums() {
    }

    @Test
    void updateAlbum() {
    }

    @Test
    void testIfAlbumDeletedFromEntityNotFoundException() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> albumService.deleteAlbum(1L));
    }

    @Test
    void testIfAlbumDeletedFromDataValidationException() {
        Album album = Album.builder().id(2L).build();
        when(albumRepository.findById(2L)).thenReturn(Optional.of(album));
        when(userContext.getUserId()).thenReturn(1L);
        assertThrows(DataValidationException.class, () -> albumService.deleteAlbum(2L));
    }
}