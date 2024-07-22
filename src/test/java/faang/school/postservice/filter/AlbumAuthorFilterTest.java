package faang.school.postservice.filter;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.filter.album.AlbumAuthorFilter;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlbumAuthorFilterTest {

    private Album appropriateAlbum;
    private Album nonAppropriateAlbum;
    private AlbumAuthorFilter albumAuthorFilter;

    @BeforeEach
    public void setUp() {
        albumAuthorFilter = new AlbumAuthorFilter();
        appropriateAlbum = Album.builder()
                .authorId(1L)
                .build();
        nonAppropriateAlbum = Album.builder()
                .authorId(2L)
                .build();
    }

    @Test
    @DisplayName("testing isApplicable method with non appropriate value")
    void testIsApplicableNonAppropriateValue() {
        AlbumFilterDto albumFilterDto = new AlbumFilterDto();
        assertFalse(albumAuthorFilter.isApplicable(albumFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable method with appropriate value")
    void testIsApplicableWithAppropriateValue() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .authorIdList(new ArrayList<>()).build();
        assertTrue(albumAuthorFilter.isApplicable(albumFilterDto));
    }

    @Test
    @DisplayName("testing filter method")
    void testFilter() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .authorIdList(List.of(1L)).build();
        List<Album> appropriateAlbumList = albumAuthorFilter
                .filter(Stream.of(appropriateAlbum, nonAppropriateAlbum), albumFilterDto)
                .toList();
        assertEquals(1, appropriateAlbumList.size());
        assertEquals(appropriateAlbum, appropriateAlbumList.get(0));
    }
}