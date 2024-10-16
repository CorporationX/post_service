package faang.school.postservice.filter.album;

import faang.school.postservice.model.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumDescriptionFilterTest {

    private AlbumDescriptionFilter albumDescriptionFilter;

    @BeforeEach
    void setUp() {
        albumDescriptionFilter = new AlbumDescriptionFilter();
    }

    @Test
    public void testIsApplicable_WithDescription() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setDescription("Test Description");

        boolean result = albumDescriptionFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicable_WithoutDescription() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setDescription(null);

        boolean result = albumDescriptionFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testApply_WithMatchingDescription() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setDescription("Test Description");

        Album album2 = new Album();
        album2.setId(2L);
        album2.setDescription("Another Description");

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setDescription("Test Description");

        Stream<Album> albumStream = Stream.of(album1, album2);

        List<Album> result = albumDescriptionFilter.apply(albumStream, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testApply_WithNoMatchingDescription() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setDescription("Test Description");

        Album album2 = new Album();
        album2.setId(2L);
        album2.setDescription("Another Description");

        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setDescription("Non-Existing Description");

        Stream<Album> albumStream = Stream.of(album1, album2);

        List<Album> result = albumDescriptionFilter.apply(albumStream, filterDto).toList();

        assertTrue(result.isEmpty());
    }
}

