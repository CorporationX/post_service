package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumCreatedBeforeFilterTest {

    private AlbumCreatedBeforeFilter albumCreatedBeforeFilter;

    @BeforeEach
    void setUp() {
        albumCreatedBeforeFilter = new AlbumCreatedBeforeFilter();
    }

    @Test
    public void testIsApplicable_WithCreatedBefore() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setCreatedBefore(LocalDateTime.now());

        boolean result = albumCreatedBeforeFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicable_WithoutCreatedBefore() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setCreatedBefore(null);

        boolean result = albumCreatedBeforeFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testApply_WithMatchingCreatedBefore() {
        LocalDateTime filterDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        Album album1 = new Album();
        album1.setId(1L);
        album1.setCreatedAt(LocalDateTime.of(2022, 12, 31, 0, 0));

        Album album2 = new Album();
        album2.setId(2L);
        album2.setCreatedAt(LocalDateTime.of(2023, 1, 2, 0, 0));

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setCreatedBefore(filterDate);

        Stream<Album> albumStream = Stream.of(album1, album2);

        List<Album> result = albumCreatedBeforeFilter.apply(albumStream, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testApply_WithAllAfterCreatedBefore() {
        LocalDateTime filterDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        Album album1 = new Album();
        album1.setId(1L);
        album1.setCreatedAt(LocalDateTime.of(2023, 1, 2, 0, 0));

        Album album2 = new Album();
        album2.setId(2L);
        album2.setCreatedAt(LocalDateTime.of(2023, 1, 3, 0, 0));

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setCreatedBefore(filterDate);

        Stream<Album> albumStream = Stream.of(album1, album2);

        List<Album> result = albumCreatedBeforeFilter.apply(albumStream, filterDto).toList();

        assertTrue(result.isEmpty());
    }
}

