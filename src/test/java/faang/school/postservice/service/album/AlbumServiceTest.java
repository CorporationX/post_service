package faang.school.postservice.service.album;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private AlbumMapper albumMapper;

    @InjectMocks
    private AlbumService albumService;

    @Test
    public void testGetAlbum_WithExistingId_Test() {
        long existingId = 1L;
        Album existingAlbum = new Album();
        existingAlbum.setId(existingId);
        AlbumDto expectedDto = AlbumDto.builder()
                .id(existingId)
                .build();

        when(albumRepository.findById(existingId)).thenReturn(Optional.of(existingAlbum));
        when(albumMapper.toDto(existingAlbum)).thenReturn(expectedDto);

        AlbumDto result = albumService.getAlbum(existingId);

        assertEquals(expectedDto, result);
        verify(albumRepository).findById(existingId);
        verify(albumMapper).toDto(existingAlbum);
    }

    @Test
    public void testGetAlbum_WithNonExistingId_Test() {
        long nonExistingId = 999L;

        when(albumRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        AlbumException albumException = assertThrows(AlbumException.class, () -> albumService.getAlbum(nonExistingId));
        assertEquals(albumException.getMessage(), "There is no album with such id");
        verify(albumRepository).findById(nonExistingId);
        verify(albumMapper, never()).toDto(any());
    }
}
