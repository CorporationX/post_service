package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumTitleFilterTest {

    private AlbumTitleFilter albumTitleFilter;

    @BeforeEach
    void setUp() {
        albumTitleFilter = new AlbumTitleFilter();
    }

    @Test
    public void testIsApplicable_WithTitle() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setTitle("Test Title");

        boolean result = albumTitleFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicable_WithoutTitle() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setTitle(null);

        boolean result = albumTitleFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testApply_WithMatchingTitle() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setTitle("Test Title");

        Album album2 = new Album();
        album2.setId(2L);
        album2.setTitle("Another Title");

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setTitle("Test Title");

        Stream<Album> albumStream = Stream.of(album1, album2);

        List<Album> result = albumTitleFilter.apply(albumStream, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Test Title", result.get(0).getTitle());
    }

    @Test
    public void testApply_WithNoMatchingTitle() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setTitle("Test Title");

        Album album2 = new Album();
        album2.setId(2L);
        album2.setTitle("Another Title");

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setTitle("Non-Existing Title");

        Stream<Album> albumStream = Stream.of(album1, album2);

        List<Album> result = albumTitleFilter.apply(albumStream, filterDto).toList();

        assertTrue(result.isEmpty());
    }
}

