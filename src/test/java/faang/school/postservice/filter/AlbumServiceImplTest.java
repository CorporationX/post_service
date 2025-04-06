package faang.school.postservice.filter;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.AlbumServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumMapper albumMapper;
    @Mock
    private AlbumSpecifications albumSpecifications;
    @InjectMocks
    private AlbumServiceImpl albumService;

    @Test
    void testCreate_Success() {
        AlbumDto dto = new AlbumDto(1L, "title", "desc", 1L);
        Album album = new Album();
        when(albumRepository.existsByAuthorIdAndTitle(1L, "title")).thenReturn(false);
        when(albumMapper.toAlbum(dto)).thenReturn(album);
        when(albumRepository.save(album)).thenReturn(album);
        when(albumMapper.toAlbumDto(album)).thenReturn(dto);
        AlbumDto result = albumService.create(dto);
        assertEquals(dto, result);
    }

    @Test
    void testCreate_AlbumExists() {
        AlbumDto dto = new AlbumDto(1L, "title", "desc", 1L);
        when(albumRepository.existsByAuthorIdAndTitle(1L, "title")).thenReturn(true);
        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> albumService.create(dto));
        assertEquals("The album already exists", ex.getMessage());
    }

    @Test
    void testCreate_EmptyDescription() {
        AlbumDto dto = new AlbumDto(1L, "title", "", 1L);
        when(albumRepository.existsByAuthorIdAndTitle(1L, "title")).thenReturn(false);
        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> albumService.create(dto));
        assertEquals("the description is empty", ex.getMessage());
    }

    @Test
    void testExistsById_ReturnsTrue() {
        when(albumRepository.existsById(5L)).thenReturn(true);
        assertTrue(albumService.existsById(5L));
    }

    @Test
    void testFindWithFilter_ReturnsAlbums() {
        AlbumFilterDto dto = new AlbumFilterDto("pattern");
        Specification<Album> spec = (root, query, cb) -> null;
        List<Album> mockAlbums = List.of(new Album(), new Album());
        when(albumSpecifications.getSpecification(dto)).thenReturn(spec);
        when(albumRepository.findAll(spec)).thenReturn(mockAlbums);
        List<Album> result = albumService.findWithFilter(dto);
        assertEquals(mockAlbums, result);
        verify(albumSpecifications).getSpecification(dto);
        verify(albumRepository).findAll(spec);
    }
}

