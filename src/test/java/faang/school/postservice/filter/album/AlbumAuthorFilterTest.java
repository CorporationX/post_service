package faang.school.postservice.filter.album;

import faang.school.postservice.model.dto.AlbumFilterDto;
import faang.school.postservice.model.entity.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumAuthorFilterTest {

    private AlbumAuthorFilter albumAuthorFilter;

    @BeforeEach
    void setUp() {
        albumAuthorFilter = new AlbumAuthorFilter();
    }

    @Test
    public void testIsApplicable_WithAuthorId() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setAuthorId(1L);

        boolean result = albumAuthorFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicable_WithoutAuthorId() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setAuthorId(null);

        boolean result = albumAuthorFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testApply_WithMatchingAuthorId() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setAuthorId(1L);

        Album album2 = new Album();
        album2.setId(2L);
        album2.setAuthorId(2L);

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setAuthorId(1L);

        Stream<Album> albumStream = Stream.of(album1, album2);

        List<Album> result = albumAuthorFilter.apply(albumStream, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getAuthorId());
    }

    @Test
    public void testApply_WithoutMatchingAuthorId() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setAuthorId(1L);

        Album album2 = new Album();
        album2.setId(2L);
        album2.setAuthorId(2L);

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setAuthorId(3L);

        Stream<Album> albumStream = Stream.of(album1, album2);

        List<Album> result = albumAuthorFilter.apply(albumStream, filterDto).toList();

        assertTrue(result.isEmpty());
    }
}

