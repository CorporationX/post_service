package faang.school.postservice.service.album;

import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.AlbumController;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    @InjectMocks
    private AlbumController albumController;
    @Mock
    private AlbumService albumService;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private AlbumMapper albumMapper;

    @BeforeEach
    void setUp() {
        userContext = new UserContext();
        userContext.setUserId(1L);
    }

    @Test
    void testAlbumNull() {
        Long albumId = 1L;
        Long userId = 1L;
        Album album = new Album();
        album.setAuthorId(userId);
        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.of(album));
        when(albumMapper.toDto(album)).thenReturn(new AlbumDto());

        AlbumDto result = albumService.getAlbum(albumId);

        assertEquals(AlbumDto.class, result.getClass());
        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findByIdWithPosts(albumId);
        verify(albumMapper, times(1)).toDto(album);
    }
}