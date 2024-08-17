package faang.school.postservice.filter;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlbumFromDateFilterTest {

    private Album appropriateAlbum;
    private Album nonAppropriateAlbum;
    private AlbumFilterDto albumFilterDto;
    private AlbumFromDateFilter albumFromDateFilter;

    @BeforeEach
    public void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .fromDate(LocalDateTime.now().minusDays(2)).build();
        albumFromDateFilter = new AlbumFromDateFilter();
        appropriateAlbum = Album.builder()
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
        nonAppropriateAlbum = Album.builder()
                .updatedAt(LocalDateTime.now().minusDays(10))
                .build();
    }

    @Test
    @DisplayName("testing isApplicable method with non appropriate value")
    void testIsApplicableNonAppropriateValue() {
        AlbumFilterDto nonAppropriateAlbumFilterDto = new AlbumFilterDto();
        assertFalse(albumFromDateFilter.isApplicable(nonAppropriateAlbumFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable method with appropriate value")
    void testIsApplicableWithAppropriateValue() {
        assertTrue(albumFromDateFilter.isApplicable(albumFilterDto));
    }

    @Test
    @DisplayName("testing filter method")
    void testFilter() {
        List<Album> appropriateAlbumList = albumFromDateFilter
                .filter(Stream.of(appropriateAlbum, nonAppropriateAlbum), albumFilterDto)
                .toList();
        assertEquals(1, appropriateAlbumList.size());
        assertEquals(appropriateAlbum, appropriateAlbumList.get(0));
    }
}