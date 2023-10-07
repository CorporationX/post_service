package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumCreatedAtFilterTest {

    private AlbumCreatedAtFilter albumCreatedAtFilter;
    private List<Album> albums;

    @BeforeEach
    public void setUp() {
        albumCreatedAtFilter = new AlbumCreatedAtFilter();
        albums = new ArrayList<>();

        Album album1 = new Album();
        album1.setCreatedAt(LocalDateTime.of(2023, 8, 15, 0, 0));
        albums.add(album1);

        Album album2 = new Album();
        album2.setCreatedAt(LocalDateTime.of(2023, 9, 1, 0, 0));
        albums.add(album2);

        Album album3 = new Album();
        album3.setCreatedAt(LocalDateTime.of(2023, 9, 5, 0, 0));
        albums.add(album3);
    }

    @Test
    public void testIsApplicableWithNonNullCreatedAt() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setCreatedAt(LocalDate.now());

        assertTrue(albumCreatedAtFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableWithNullCreatedAt() {
        AlbumFilterDto filterDto = new AlbumFilterDto();

        assertFalse(albumCreatedAtFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        LocalDate filterDate = LocalDate.of(2023, 9, 1);
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setCreatedAt(filterDate);

        albumCreatedAtFilter.apply(albums, filterDto);

        for (Album album : albums) {
            assertTrue(album.getCreatedAt().toLocalDate().isEqual(filterDate));
        }
    }
}
