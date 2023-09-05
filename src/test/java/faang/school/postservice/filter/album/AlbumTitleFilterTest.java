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

public class AlbumTitleFilterTest {

    private AlbumTitleFilter albumTitleFilter;
    private List<Album> albums;

    @BeforeEach
    void setUp() {
        albumTitleFilter = new AlbumTitleFilter();
        albums = new ArrayList<>();
        albums.add(Album.builder().title("Album 1").build());
        albums.add(Album.builder().title("Album 2").build());
        albums.add(Album.builder().title("Album 3").build());
    }

    @Test
    void testIsApplicableWithTitle() {
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        albumFilterDto.setTitle("Album 1");

        boolean isApplicable = albumTitleFilter.isApplicable(albumFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicableWithoutTitle() {
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();

        boolean isApplicable = albumTitleFilter.isApplicable(albumFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void testApplyFilter() {
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        albumFilterDto.setTitle("Album 1");

        albumTitleFilter.apply(albums, albumFilterDto);

        assertEquals(1, albums.size());
        assertEquals("Album 1", albums.get(0).getTitle());
    }

    @Test
    void testApplyFilterWithoutMatch() {
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        albumFilterDto.setTitle("Non-existent Album");

        albumTitleFilter.apply(albums, albumFilterDto);

        assertEquals(0, albums.size());
    }
}
