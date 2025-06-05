package faang.school.postservice.filter;

import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.filter.album.FromDateFilter;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class AlbumFromDateFilterTest {

    @InjectMocks
    FromDateFilter fromDateFilter;

    private AlbumFilterDto filterDto;
    private Album albumBefore;
    private Album albumAfter;

    @BeforeEach
    void setUp() {
        filterDto = new AlbumFilterDto();

        albumBefore = Album.builder()
                .id(1L)
                .title("Old Album")
                .createdAt(LocalDateTime.of(2022, 1, 1, 0, 0))
                .build();

        albumAfter = Album.builder()
                .id(2L)
                .title("New Album")
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenFromDateIsSet() {
        filterDto.setFromDate("2023-01-01");
        assertTrue(fromDateFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenFromDateIsNull() {
        filterDto.setFromDate(null);
        assertFalse(fromDateFilter.isApplicable(filterDto));
    }

    @Test
    void apply_ShouldIncludeAlbumsAfterOrEqualToFromDate() {
        filterDto.setFromDate("2023-01-01");

        List<Album> result = fromDateFilter
                .apply(Stream.of(albumAfter, albumBefore), filterDto)
                .toList();

        assertEquals(1, result.size());
        assertEquals("New Album", result.get(0).getTitle());
    }

    @Test
    void apply_ShouldIncludeAlbum_WhenCreatedOnFromDate() {
        filterDto.setFromDate("2023-01-01");

        Album albumExact = Album.builder()
                .id(3L)
                .title("Exact Date")
                .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0))
                .build();

        List<Album> result = fromDateFilter
                .apply(Stream.of(albumExact), filterDto)
                .toList();

        assertEquals(1, result.size());
        assertEquals("Exact Date", result.get(0).getTitle());
    }
}
