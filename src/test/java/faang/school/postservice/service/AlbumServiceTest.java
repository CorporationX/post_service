package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filter.AlbumCreatedAfterFilter;
import faang.school.postservice.filter.AlbumFilter;
import faang.school.postservice.filter.AlbumTitleFilter;
import faang.school.postservice.mapper.AlbumMapperImpl;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.AlbumValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    private AlbumService albumService;

    private AlbumRepository albumRepository;
    private AlbumMapperImpl albumMapper;
    private AlbumValidator validator;
    private UserContext userContext;
    private PostService postService;
    private PostMapperImpl postMapper;
    private List<AlbumFilter> filters;

    private AlbumDto albumDto;
    private Long authorId;
    private Long userId;
    private Long albumId;
    private Long postId;
    private Album album;
    private Post post;
    private List<Album> albums;
    private AlbumFilterDto albumFilterDto;

    @BeforeEach
    void setUp() {
        albumRepository = Mockito.mock(AlbumRepository.class);
        albumMapper = Mockito.spy(AlbumMapperImpl.class);
        validator = Mockito.mock(AlbumValidator.class);
        userContext = Mockito.mock(UserContext.class);
        postService = Mockito.mock(PostService.class);
        postMapper = Mockito.mock(PostMapperImpl.class);
        filters = new ArrayList<>();
        AlbumTitleFilter albumTitleFilter = new AlbumTitleFilter();
        AlbumCreatedAfterFilter albumCreatedAfterFilter = new AlbumCreatedAfterFilter();
        filters.add(albumTitleFilter);
        filters.add(albumCreatedAfterFilter);
        albumFilterDto = AlbumFilterDto.builder().title("tit").build();

        albumService = new AlbumService(albumRepository, albumMapper, validator, userContext,
                postService, postMapper, filters);

        authorId = 1L;
        albumId = 2L;
        postId = 3L;
        userId = 10L;
        albumDto = AlbumDto.builder().title("title").description("desc").authorId(authorId).build();
        album = Album.builder().id(albumId).title("title").description("desc").authorId(authorId).posts(new ArrayList<>()).build();
        post = Post.builder().id(postId).build();


        albums = new ArrayList<>();
        albums.add(Album.builder().title("title1").description("1").build());
        albums.add(Album.builder().title("2").description("2").build());
        albums.add(Album.builder().title("3").description("3").build());
    }

    @Test
    void testCreateAlbum() {
        albumService.createAlbum(albumDto);
        verify(albumRepository, times(1)).save(albumMapper.toEntity(albumDto));
    }

    @Test
    void testAddPostAlbumNotFound() {
        when(userContext.getUserId()).thenReturn(userId);
        assertThrows(IllegalArgumentException.class,
                () -> albumService.addPost(albumId, postId));
    }

    @Test
    void testAddPost() {
        PostDto postDto = PostDto.builder().content("content").build();

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(postService.getPost(postId)).thenReturn(postDto);
        when(postMapper.toEntity(postDto)).thenReturn(post);

        albumService.addPost(albumId, postId);
        assertEquals(1, album.getPosts().size());
    }

    @Test
    void testDeletePost() {
        album.addPost(post);
        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.deletePost(albumId, postId);

        assertEquals(0, album.getPosts().size());
    }

    @Test
    void testAddToFavorite() {
        when(userContext.getUserId()).thenReturn(userId);

        albumService.addToFavorite(albumId);
        verify(albumRepository, times(1)).addAlbumToFavorites(albumId, userId);
    }

    @Test
    void testRemoveFromFavorite() {
        when(userContext.getUserId()).thenReturn(userId);

        albumService.removeFromFavorite(albumId);
        verify(albumRepository, times(1)).deleteAlbumFromFavorites(albumId, userId);
    }

    @Test
    void testGetAlbum() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        AlbumDto expected = albumMapper.toDto(album);
        assertEquals(expected, albumService.getAlbum(albumId));
    }

    @Test
    void testGetUserAlbums() {
        when(userContext.getUserId()).thenReturn(userId);
        Stream<Album> albumStream = albums.stream();
        when(albumRepository.findByAuthorId(userId)).thenReturn(albumStream);


        List<AlbumDto> albumDtos = albumService.getUserAlbums(albumFilterDto);
        assertEquals(1, albumDtos.size());
    }

    @Test
    void testGetAllAlbums() {
        Iterable<Album> iterable = albums;
        when(albumRepository.findAll()).thenReturn(iterable);

        for (Album album : albums) {
            when(albumMapper.toDto(album)).thenReturn(AlbumDto.builder()
                    .title(album.getTitle())
                    .description(album.getDescription())
                    .build());
        }

        AlbumFilterDto filterDto = AlbumFilterDto.builder().title("title").build();
        List<AlbumDto> albumDtos = albumService.getAllAlbums(filterDto);

        assertEquals(1, albumDtos.size());
        assertEquals("title1", albumDtos.get(0).getTitle());
    }

    @Test
    void getUserFavoriteAlbums() {
        when(userContext.getUserId()).thenReturn(userId);
        Stream<Album> albumStream = albums.stream();
        when(albumRepository.findByAuthorId(userId)).thenReturn(albumStream);

        List<AlbumDto> albumDtos = albumService.getUserAlbums(albumFilterDto);
        assertEquals(1, albumDtos.size());
    }

    @Test
    void testUpdateAlbum() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        AlbumDto newDto = AlbumDto.builder().title("new title").description("new desc").build();
        AlbumDto actual = albumService.updateAlbum(albumId, newDto);

        assertAll(
                () -> assertEquals(newDto.getTitle(), actual.getTitle()),
                () -> assertEquals(newDto.getDescription(), actual.getDescription())
        );
    }

    @Test
    void testDeleteAlbum() {
        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.deleteAlbum(albumId);

        verify(albumRepository, times(1)).delete(album);
    }
}


