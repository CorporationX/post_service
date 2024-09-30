package faang.school.postservice.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AlbumCreationDateFilterTest {
    AlbumCreationDateFilter albumCreationDateFilter;
    AlbumFilterDto albumFilterDto;

    @BeforeEach
    public void setUp() {
        albumCreationDateFilter = new AlbumCreationDateFilter();
        albumFilterDto = new AlbumFilterDto();
    }

    @Test
    public void testIsApplicableWithEmptyFilter() {
        boolean result = albumCreationDateFilter.isApplicable(albumFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessfully() {
        albumFilterDto.setCreatedFromDate(LocalDateTime.now());
        boolean result = albumCreationDateFilter.isApplicable(albumFilterDto);

        assertTrue(result);
    }

    @Test
    public void testApplySuccessfully() {
        albumFilterDto.setCreatedFromDate(LocalDateTime.now());
        Album album = new Album();
        album.setCreatedAt(LocalDateTime.now().plusDays(1));
        Album anotherAlbum = new Album();
        anotherAlbum.setCreatedAt(LocalDateTime.now().minusDays(1));

        Stream<Album> result = albumCreationDateFilter.apply(albumFilterDto, Stream.of(album, anotherAlbum));

        List<Album> resultList = result.toList();
        assertEquals(1, resultList.size());
        assertTrue(resultList.contains(album));
        assertFalse(resultList.contains(anotherAlbum));
    }
}
