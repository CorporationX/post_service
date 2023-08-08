package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.album.AlbumException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    private UserContext userContext;

    @InjectMocks
    private AlbumService albumService;
//    @Test
//    public void testUpdateAlbum_Success() {
//        long albumId = 1L;
//        long userId = 123L;
//
//        AlbumDto updatedDto = AlbumDto.builder()
//                .title("Updated Title")
//                .description("Updated Description")
//                .authorId(userId)
//                .postsIds(Collections.singletonList(456L))
//                .build();
//
//        Album existingAlbum = new Album();
//        existingAlbum.setId(albumId);
//        existingAlbum.setTitle("Old Title");
//        existingAlbum.setDescription("Old Description");
//        existingAlbum.setAuthorId(userId);
//        existingAlbum.setPosts(Collections.emptyList());
//
//        when(albumRepository.findById(albumId)).thenReturn(Optional.of(existingAlbum));
//        when(userContext.getUserId()).thenReturn(userId);
//        when(albumMapper.toEntity(updatedDto)).thenReturn(existingAlbum);
//
//        AlbumDto result = albumService.updateAlbum(albumId, updatedDto);
//
//        assertNotNull(result);
//        assertEquals(updatedDto.getTitle(), result.getTitle());
//        assertEquals(updatedDto.getDescription(), result.getDescription());
//        assertEquals(userId, result.getAuthorId());
//        assertEquals(1, result.getPostsIds().size());
//        assertEquals(existingAlbum.getCreatedAt(), result.getCreatedAt());
//
//    }

    @Test
    public void testUpdateAlbum_AlbumNotFound() {
        long albumId = 1L;
        AlbumDto updatedAlbumDto = AlbumDto.builder().build();

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        AlbumException albumException = assertThrows(AlbumException.class,
                () -> albumService.updateAlbum(albumId, updatedAlbumDto));

        assertEquals("Album not found", albumException.getMessage());

        verify(albumRepository, times(1)).findById(albumId);
        verify(albumRepository, never()).save(any(Album.class));
        verify(albumMapper, never()).toEntity(updatedAlbumDto);
        verify(userContext, never()).getUserId();
    }

    @Test
    public void testUpdateAlbum_DifferentAuthor() {
        long albumId = 1L;
        long authorId = 123L;

        Album existingAlbum = new Album();
        existingAlbum.setId(albumId);
        existingAlbum.setAuthorId(456L);

        AlbumDto updatedAlbumDto = AlbumDto.builder().build();
        updatedAlbumDto.setTitle("Updated Album");
        updatedAlbumDto.setDescription("Updated description");
        updatedAlbumDto.setAuthorId(authorId);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(existingAlbum));
        when(userContext.getUserId()).thenReturn(authorId);

        AlbumException albumException = assertThrows(AlbumException.class,
                () -> albumService.updateAlbum(albumId, updatedAlbumDto));
        assertEquals("You can only update your own albums", albumException.getMessage());

        verify(albumRepository, times(1)).findById(albumId);
        verify(albumRepository, never()).save(any(Album.class));
        verify(albumMapper, never()).toEntity(updatedAlbumDto);
        verify(userContext, times(1)).getUserId();
    }
}
