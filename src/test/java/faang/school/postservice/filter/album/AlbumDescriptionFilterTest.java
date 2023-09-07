package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumDescriptionFilterTest {

    private AlbumDescriptionFilter albumDescriptionFilter;
    private List<Album> albums;

    @BeforeEach
    public void setUp() {
        albumDescriptionFilter = new AlbumDescriptionFilter();
        albums = new ArrayList<>();

        Album album = Album.builder().description("Description for Album 1").build();
        albums.add(album);
        Album album1 = Album.builder().description("Description for Album 2").build();
        albums.add(album1);
        Album album3 = Album.builder().description("Description for Album 3").build();
        albums.add(album3);
    }

    @Test
    public void testIsApplicableWithNonNullDescription() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setDescription("Description for Album 1");

        assertTrue(albumDescriptionFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableWithNullDescription() {
        AlbumFilterDto filterDto = new AlbumFilterDto();

        assertFalse(albumDescriptionFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setDescription("Description for Album 1");

        albumDescriptionFilter.apply(albums, filterDto);

        assertEquals(1, albums.size());
        assertEquals("Description for Album 1", albums.get(0).getDescription());
    }

    @Test
    public void testApplyFilterWithNoMatchingDescription() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setDescription("Non-matching Description");

        albumDescriptionFilter.apply(albums, filterDto);

        assertEquals(0, albums.size());
    }
}
