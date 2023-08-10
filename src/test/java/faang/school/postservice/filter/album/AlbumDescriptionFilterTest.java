package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlbumDescriptionFilterTest {

    @Test
    public void testDescriptionFilterWithMatchingAlbum() {
        Album album = new Album(1L, "Title", "Matching Description", 1L, null, null, null);

        AlbumFilterDto filters = new AlbumFilterDto();
        filters.setDescriptionPattern("Match");

        AlbumDescriptionFilter descriptionFilter = new AlbumDescriptionFilter();

        Stream<Album> filteredAlbums = descriptionFilter.apply(Stream.of(album), filters);

        assertEquals(1L, filteredAlbums.count());
    }

    @Test
    public void testDescriptionFilterWithNonMatchingAlbum() {
        Album album = new Album(1L, "Title", "Non-Matching", 1L, null, null, null);

        AlbumFilterDto filters = new AlbumFilterDto();
        filters.setDescriptionPattern("does-not-match");

        AlbumDescriptionFilter descriptionFilter = new AlbumDescriptionFilter();

        Stream<Album> filteredAlbums = descriptionFilter.apply(Stream.of(album), filters);

        assertEquals(0L, filteredAlbums.count());
    }
}
