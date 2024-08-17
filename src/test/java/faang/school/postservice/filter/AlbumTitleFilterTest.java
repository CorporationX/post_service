package faang.school.postservice.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filter.album.AlbumTitleFilter;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlbumTitleFilterTest {
    private Album appropriateAlbum;
    private Album nonAppropriateAlbum;
    private AlbumFilterDto albumFilterDto;
    private AlbumTitleFilter albumTitleFilter;

    @BeforeEach
    public void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .titlePattern("title").build();
        albumTitleFilter = new AlbumTitleFilter();
        appropriateAlbum = Album.builder()
                .title("title")
                .build();
        nonAppropriateAlbum = Album.builder()
                .title("not")
                .build();
    }

    @Test
    @DisplayName("testing isApplicable method with non appropriate value")
    void testIsApplicableNonAppropriateValue() {
        AlbumFilterDto nonAppropriateAlbumFilterDto = new AlbumFilterDto();
        assertFalse(albumTitleFilter.isApplicable(nonAppropriateAlbumFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable method with appropriate value")
    void testIsApplicableWithAppropriateValue() {
        assertTrue(albumTitleFilter.isApplicable(albumFilterDto));
    }

    @Test
    @DisplayName("testing filter method")
    void testFilter() {
        List<Album> appropriateAlbumList = albumTitleFilter
                .filter(Stream.of(appropriateAlbum, nonAppropriateAlbum), albumFilterDto)
                .toList();
        assertEquals(1, appropriateAlbumList.size());
        assertEquals(appropriateAlbum, appropriateAlbumList.get(0));
    }
}