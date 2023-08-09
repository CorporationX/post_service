package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.filter.album_filter.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private  AlbumRepository albumRepository;
    @Mock
    private  UserServiceClient userServiceClient;
    @Spy
    private  AlbumMapper albumMapper;
    @Mock
    private  UserContext userContext;
    @Mock
    private  PostRepository postRepository;
    @Mock
    private  List<AlbumFilter> albumFilters;
    @InjectMocks
    private  AlbumService albumService;

    @Test
    void createAlbum() {
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
    void deleteAlbum() {
    }
}