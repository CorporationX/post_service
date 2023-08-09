package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.album.AlbumException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumMapper albumMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserContext userContext;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private AlbumService albumService;

    @Test
    public void deletePostFromAlbum_ValidInput_PostDeleted_Test() {
        long userId = 1L;
        long albumId = 1L;
        long postIdToDelete = 123L;

        Album album = new Album();
        album.setAuthorId(userId);
        ArrayList<Post> posts = new ArrayList<>();
        posts.add(Post.builder().id(postIdToDelete).build());
        album.setPosts(posts);

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(postRepository.findById(postIdToDelete)).thenReturn(Optional.of(Post.builder().id(postIdToDelete).build()));

        albumService.deletePostFromAlbum(albumId, postIdToDelete);

        assertEquals(0, album.getPosts().size());
    }

    @Test
    public void deletePostFromAlbum_PostNotInAlbum_AlbumExceptionThrown_Test() {
        long userId = 1L;
        long albumId = 1L;
        long postIdToDelete = 123L;

        Album album = new Album();
        album.setAuthorId(userId);
        ArrayList<Post> posts = new ArrayList<>();
        posts.add(Post.builder().id(456L).build());
        album.setPosts(posts);

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(postRepository.findById(postIdToDelete)).thenReturn(Optional.of(Post.builder().id(postIdToDelete).build()));

        AlbumException albumException = assertThrows(AlbumException.class,
                () -> albumService.deletePostFromAlbum(albumId, postIdToDelete));
        assertEquals("Post with id=123 is not found in album", albumException.getMessage());
        verify(albumRepository, never()).save(any());
    }


    @Test
    public void addPostToAlbum_AlbumNotFound_AlbumExceptionThrown_Test() {
        long userId = 1L;
        long albumId = 1L;
        long postIdToAdd = 123L;

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        AlbumException albumException = assertThrows(AlbumException.class,
                () -> albumService.addPostToAlbum(albumId, postIdToAdd));

        assertEquals("There is no album with such id", albumException.getMessage());
        verify(albumRepository, never()).save(any());
    }

    @Test
    public void addPostToAlbum_UserNotAuthorized_AlbumExceptionThrown_Test() {
        long userId = 1L;
        long albumId = 1L;
        long postIdToAdd = 123L;

        Album album = new Album();
        album.setAuthorId(userId + 1);

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        AlbumException albumException = assertThrows(AlbumException.class,
                () -> albumService.addPostToAlbum(albumId, postIdToAdd));

        assertEquals("You can perform this action only on your albums", albumException.getMessage());
        verify(albumRepository, never()).save(any());
    }

    @Test
    public void addPostToAlbum_PostAlreadyExists_AlbumExceptionThrown_Test() {
        long userId = 1L;
        long albumId = 1L;
        long postIdToAdd = 123L;

        Album album = new Album();
        album.setAuthorId(userId);
        List<Post> posts = new ArrayList<>();
        posts.add(Post.builder().id(postIdToAdd).build());
        album.setPosts(posts);

        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        AlbumException albumException = assertThrows(AlbumException.class,
                () -> albumService.addPostToAlbum(albumId, postIdToAdd));

        assertEquals("Post with id=123 is already added in album", albumException.getMessage());
        verify(albumRepository, never()).save(any());
    }

    @Test
    public void testCreateAlbum() {
        UserDto mockUserDto = getMockUserDto();
        when(userServiceClient.getUser(1L)).thenReturn(mockUserDto);

        AlbumDto albumDto = getValidAlbumDto();
        Album mockAlbum = Album.builder()
                .id(1L)
                .title(albumDto.getTitle())
                .description(albumDto.getDescription())
                .authorId(albumDto.getAuthorId())
                .build();
        when(albumMapper.toEntity(albumDto)).thenReturn(mockAlbum);
        when(albumRepository.save(any(Album.class))).thenReturn(mockAlbum);
        when(albumMapper.toDto(mockAlbum)).thenReturn(albumDto);

        AlbumDto createdAlbum = albumService.createAlbum(albumDto);

        verify(userServiceClient, times(1)).getUser(1L);
        verify(albumRepository, times(1)).save(any(Album.class));
        verify(albumMapper, times(1)).toDto(mockAlbum);

        assertNotNull(createdAlbum);
        assertEquals(albumDto.getTitle(), createdAlbum.getTitle());
        assertEquals(albumDto.getDescription(), createdAlbum.getDescription());
        assertEquals(albumDto.getAuthorId(), createdAlbum.getAuthorId());
    }

    @Test
    public void testCreateAlbum_InvalidAuthor() {
        when(userServiceClient.getUser(1L)).thenReturn(null);

        AlbumException exception = assertThrows(AlbumException.class,
                () -> albumService.createAlbum(getValidAlbumDto()));

        assertEquals("There is no user with id 1", exception.getMessage());
    }

    @Test
    public void testCreateAlbum_NonUniqueTitle() {
        UserDto mockUserDto = getMockUserDto();
        when(userServiceClient.getUser(1L)).thenReturn(mockUserDto);

        AlbumDto albumDto = getValidAlbumDto();
        Album existingAlbum = Album.builder()
                .id(2L)
                .title(albumDto.getTitle())
                .description("Another album with the same title")
                .authorId(albumDto.getAuthorId())
                .build();
        when(albumRepository.findByAuthorId(1L)).thenReturn(Stream.of(existingAlbum));

        AlbumException exception = assertThrows(AlbumException.class,
                () -> albumService.createAlbum(albumDto));

        assertEquals("Title of the album should be unique", exception.getMessage());
    }

    @Test
    public void getAlbum_ShouldReturnUser_WhenExists_Test() {
        long existingId = 1L;
        Album existingAlbum = new Album();
        existingAlbum.setId(existingId);
        AlbumDto expectedDto = AlbumDto.builder().build();

        when(albumRepository.findById(existingId)).thenReturn(Optional.of(existingAlbum));
        when(albumMapper.toDto(existingAlbum)).thenReturn(expectedDto);

        AlbumDto result = albumService.getAlbum(existingId);

        assertEquals(expectedDto, result);
        verify(albumRepository).findById(existingId);
        verify(albumMapper).toDto(existingAlbum);
    }


    @Test
    public void getAlbum_ShouldThrowAlbumException_WhenAlbumNotFound_Test() {
        long nonExistingId = 999L;

        when(albumRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        AlbumException albumException = assertThrows(AlbumException.class, () -> albumService.getAlbum(nonExistingId));
        assertEquals(albumException.getMessage(), "There is no album with id = " + nonExistingId);
        verify(albumRepository).findById(nonExistingId);
        verify(albumMapper, never()).toDto(any());
    }

    private UserDto getMockUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .build();
    }

    private AlbumDto getValidAlbumDto() {
        return AlbumDto.builder()
                .title("Test Album")
                .description("Album description")
                .authorId(1L)
                .build();
    }
}
