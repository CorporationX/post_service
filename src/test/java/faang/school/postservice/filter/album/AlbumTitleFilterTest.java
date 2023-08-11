package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlbumTitleFilterTest {

    @Test
    public void testTitleFilterWithMatchingAlbum() {
        Album album = new Album(1L, "Matching Title", "Description", 1L, null, null, null);

        AlbumFilterDto filters = new AlbumFilterDto();
        filters.setTitlePattern("Match");

        AlbumTitleFilter titleFilter = new AlbumTitleFilter();

        Stream<Album> filteredAlbums = titleFilter.apply(Stream.of(album), filters);

        assertEquals(1L, filteredAlbums.count());
    }

    @Test
    public void testTitleFilterWithNonMatchingAlbum() {
        Album album = new Album(1L, "Non-Matching", "Description", 1L, null, null, null);

        AlbumFilterDto filters = new AlbumFilterDto();
        filters.setTitlePattern("does-not-match");

        AlbumTitleFilter titleFilter = new AlbumTitleFilter();

        Stream<Album> filteredAlbums = titleFilter.apply(Stream.of(album), filters);

        assertEquals(0L, filteredAlbums.count());
    }
}
