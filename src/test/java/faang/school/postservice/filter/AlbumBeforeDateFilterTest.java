package faang.school.postservice.filter;

import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.filter.album.BeforeDateFilter;
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
public class AlbumBeforeDateFilterTest {

    @InjectMocks
    BeforeDateFilter beforeDateFilter;

    private AlbumFilterDto filterDto;
    private Album albumBefore;
    private Album albumAfter;

    @BeforeEach
    void setUp() {
        filterDto = new AlbumFilterDto();

        albumBefore = Album.builder()
                .id(1L)
                .title("Old Album")
                .createdAt(LocalDateTime.parse("2022-01-01T00:00:00"))
                .build();

        albumAfter = Album.builder()
                .id(2L)
                .title("New Album")
                .createdAt(LocalDateTime.parse("2024-01-01T00:00:00"))
                .build();
    }

    @Test
    void isApplicable_ShouldReturnTrueWhenBeforeDateIsSet() {
        filterDto.setBeforeDate("2023-01-01");
        assertTrue(beforeDateFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenBeforeDateIsNull() {
        filterDto.setBeforeDate(null);
        assertFalse(beforeDateFilter.isApplicable(filterDto));
    }

    @Test
    void apply_ShouldFilterAlbumsBeforeOrEqualToGivenDate() {
        filterDto.setBeforeDate("2023-01-01");

        List<Album> result = beforeDateFilter
                .apply(Stream.of(albumBefore, albumAfter), filterDto)
                .toList();

        assertEquals(1, result.size());
        assertEquals("Old Album", result.get(0).getTitle());
    }
}
