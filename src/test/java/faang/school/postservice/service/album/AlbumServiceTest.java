package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.album.AlbumException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.album.AlbumValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumMapper albumMapper;
    @Mock
    private AlbumValidator albumValidator;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private AlbumService albumService;

    @Test
    public void addPostToAlbum_PostAlreadyExist_Test() {
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(albumValidator.addPostToAlbumValidateService(1, 1, 1)).thenReturn(Album.builder()
                .build());

        Mockito.when(albumMapper.toDto(Album.builder()
                .build())).thenReturn(AlbumDto.builder()
                .postsIds(List.of(1L, 2L, 3L))
                .build());

        AlbumException albumException = Assertions.assertThrows(AlbumException.class,
                () -> albumService.addPostToAlbum(1, 1));

        Assertions.assertEquals(albumException.getMessage(), "this post already exist in album");
    }

    @Test
    public void addPostToAlbum_Test() {
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Album album = Album.builder().build();

        AlbumDto albumDto = AlbumDto.builder()
                .postsIds(new ArrayList<>(List.of(1L, 2L, 3L)))
                .build();

        Mockito.when(albumValidator.addPostToAlbumValidateService(1, 1, 4))
                .thenReturn(album);

        Mockito.when(albumMapper.toDto(album)).thenReturn(albumDto);

        albumService.addPostToAlbum(1, 4);

        Mockito.verify(albumMapper).toDto(album);
    }

    @Test
    public void deleteUnexistingPost_Test() {
        Album album = Album.builder()
                .posts(new ArrayList<>(List.of(Post.builder()
                                .id(4)
                        .build())))
                .build();
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(albumValidator.addPostToAlbumValidateService(1, 1, 1))
                .thenReturn(album);

        List<Post> posts = album.getPosts();
        List<Long> postsIds = posts.stream().map(Post::getId).toList();
        int id = 1;

        AlbumException albumException = Assertions.assertThrows(AlbumException.class,
                () -> albumService.deletePostFromAlbum(1L, id));

        Assertions.assertEquals(albumException.getMessage(),"Undefined post");
    }
}
