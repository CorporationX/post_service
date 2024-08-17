package faang.school.postservice.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filter.album.AlbumDescriptionFilter;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlbumDescriptionFilterTest {

    private Album appropriateAlbum;
    private Album nonAppropriateAlbum;
    private AlbumDescriptionFilter albumDescriptionFilter;

    @BeforeEach
    public void setUp() {
        albumDescriptionFilter = new AlbumDescriptionFilter();
        appropriateAlbum = Album.builder()
                .description("description")
                .build();
        nonAppropriateAlbum = Album.builder()
                .description("not")
                .build();
    }

    @Test
    @DisplayName("testing isApplicable method with non appropriate value")
    void testIsApplicableNonAppropriateValue() {
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        assertFalse(albumDescriptionFilter.isApplicable(albumFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable method with appropriate value")
    void testIsApplicableWithAppropriateValue() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .descriptionPattern("description").build();
        assertTrue(albumDescriptionFilter.isApplicable(albumFilterDto));
    }

    @Test
    @DisplayName("testing filter method")
    void testFilter() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .descriptionPattern("description").build();
        List<Album> appropriateAlbumList = albumDescriptionFilter
                .filter(Stream.of(appropriateAlbum, nonAppropriateAlbum), albumFilterDto)
                .toList();
        assertEquals(1, appropriateAlbumList.size());
        assertEquals(appropriateAlbum, appropriateAlbumList.get(0));
    }
}