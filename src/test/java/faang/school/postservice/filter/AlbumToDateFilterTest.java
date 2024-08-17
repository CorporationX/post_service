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

class AlbumToDateFilterTest {

    private Album appropriateAlbum;
    private Album nonAppropriateAlbum;
    private AlbumFilterDto albumFilterDto;
    private AlbumToDateFilter albumToDateFilter;

    @BeforeEach
    public void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .toDate(LocalDateTime.now().minusDays(2)).build();
        albumToDateFilter = new AlbumToDateFilter();
        appropriateAlbum = Album.builder()
                .updatedAt(LocalDateTime.now().minusDays(10))
                .build();
        nonAppropriateAlbum = Album.builder()
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    @DisplayName("testing isApplicable method with non appropriate value")
    void testIsApplicableNonAppropriateValue() {
        AlbumFilterDto nonAppropriateAlbumFilterDto = new AlbumFilterDto();
        assertFalse(albumToDateFilter.isApplicable(nonAppropriateAlbumFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable method with appropriate value")
    void testIsApplicableWithAppropriateValue() {
        assertTrue(albumToDateFilter.isApplicable(albumFilterDto));
    }

    @Test
    @DisplayName("testing filter method")
    void testFilter() {
        List<Album> appropriateAlbumList = albumToDateFilter
                .filter(Stream.of(appropriateAlbum, nonAppropriateAlbum), albumFilterDto)
                .toList();
        assertEquals(1, appropriateAlbumList.size());
        assertEquals(appropriateAlbum, appropriateAlbumList.get(0));
    }
}