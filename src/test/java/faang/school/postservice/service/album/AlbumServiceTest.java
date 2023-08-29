package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidException;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    @InjectMocks
    private AlbumService albumService;
    private Album album1;
    private Album album2;
    private Album album3;
    private Album album4;


    @BeforeEach
    void setUp() {
        album1 = Album.builder().id(1L).title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        album2 = Album.builder().id(1L).title(" ").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        album3 = Album.builder().id(1L).title("title").authorId(1L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();
        album4 = Album.builder().id(1L).title("title").authorId(2L).createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX).build();

    }

    @Test
    void testCreateAlbum_DataValidException() {
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder().description("description").title("title").authorId(1L).build();
        assertThrows(DataValidException.class, () -> albumService.createAlbum(albumCreateDto));
    }

    @Test
    void testCreateAlbumUser_DataValidationException() {
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder().description("description").title("title").authorId(1L).build();

        when(userServiceClient.getUser(albumCreateDto.getAuthorId())).thenReturn(UserDto.builder().id(1L).build());
        when(albumRepository.existsByTitleAndAuthorId(albumCreateDto.getTitle(), albumCreateDto.getAuthorId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> albumService.createAlbum(albumCreateDto));
    }

    @Test
    void testCreateAlbum() {
        Album album = Album.builder().id(1L).build();
        AlbumDto albumDto = AlbumDto.builder().id(1L).build();
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder().description("description").title("title").authorId(1L).build();

        when(userServiceClient.getUser(albumCreateDto.getAuthorId())).thenReturn(UserDto.builder().id(1L).build());
        when(albumRepository.existsByTitleAndAuthorId(anyString(), anyLong())).thenReturn(false);

        when(albumMapper.toAlbumCreate(albumCreateDto)).thenReturn(album);
        when(albumMapper.toAlbumDto(album)).thenReturn(albumDto);
        when(albumRepository.save(album)).thenReturn(album);

        albumService.createAlbum(albumCreateDto);
        verify(albumRepository, times(1)).save(any());
    }
}