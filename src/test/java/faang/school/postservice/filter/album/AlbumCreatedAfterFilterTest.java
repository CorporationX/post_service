package faang.school.postservice.filter.album;

import faang.school.postservice.model.dto.AlbumFilterDto;
import faang.school.postservice.model.entity.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumCreatedAfterFilterTest {

    private AlbumCreatedAfterFilter albumCreatedAfterFilter;

    @BeforeEach
    void setUp() {
        albumCreatedAfterFilter = new AlbumCreatedAfterFilter();
    }

    @Test
    public void testIsApplicable_WithCreatedAfter() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setCreatedAfter(LocalDateTime.now());

        boolean result = albumCreatedAfterFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicable_WithoutCreatedAfter() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setCreatedAfter(null);

        boolean result = albumCreatedAfterFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testApply_WithMatchingCreatedAfter() {
        LocalDateTime filterDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        Album album1 = new Album();
        album1.setId(1L);
        album1.setCreatedAt(LocalDateTime.of(2023, 1, 5, 0, 0));

        Album album2 = new Album();
        album2.setId(2L);
        album2.setCreatedAt(LocalDateTime.of(2022, 12, 31, 0, 0));

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setCreatedAfter(filterDate);

        Stream<Album> albumStream = Stream.of(album1, album2);

        List<Album> result = albumCreatedAfterFilter.apply(albumStream, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testApply_WithAllBeforeCreatedAfter() {
        LocalDateTime filterDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        Album album1 = new Album();
        album1.setId(1L);
        album1.setCreatedAt(LocalDateTime.of(2022, 12, 31, 0, 0));

        Album album2 = new Album();
        album2.setId(2L);
        album2.setCreatedAt(LocalDateTime.of(2022, 12, 30, 0, 0));

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setCreatedAfter(filterDate);

        Stream<Album> albumStream = Stream.of(album1, album2);

        List<Album> result = albumCreatedAfterFilter.apply(albumStream, filterDto).toList();

        assertTrue(result.isEmpty());
    }
}

