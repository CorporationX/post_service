package faang.school.postservice.filter;


import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumFilterTest {

    private List<AlbumFilter> filters;
    private AlbumTitleFilter albumTitleFilter;
    private AlbumCreatedAfterFilter albumCreatedAfterFilter;
    private List<AlbumDto> albums;
    private LocalDateTime now;

    @BeforeEach
    public void setUp() {
        albumTitleFilter = new AlbumTitleFilter();
        albumCreatedAfterFilter = new AlbumCreatedAfterFilter();
        now = LocalDateTime.now();

        filters = List.of(albumTitleFilter, albumCreatedAfterFilter);

        albums = new ArrayList<>();
        albums.add(AlbumDto.builder().title("qweqwe").description("qwe").build());
        albums.add(AlbumDto.builder().title("qweqwe").description("qwe").createdAt(now.minusMonths(2)).build());
        albums.add(AlbumDto.builder().title("123").description("qwe").createdAt(now.minusMonths(1)).build());
        albums.add(AlbumDto.builder().title("qweqwe").description("qwe").createdAt(now.minusYears(1)).build());
    }

    @Test
    void testIsApplicableWithTitle() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder().title("qwe").build();
        assertTrue(albumTitleFilter.isApplicable(albumFilterDto));
    }

    @Test
    void testIsApplicableWithCreatedTime() {
        AlbumFilterDto filterDto = AlbumFilterDto.builder().createdAfter(LocalDateTime.now()).build();
        assertTrue(albumCreatedAfterFilter.isApplicable(filterDto));
    }

    @Test
    void testApply() {
        AlbumFilterDto filterDto = AlbumFilterDto.builder()
                .title("qweqwe")
                .createdAfter(now.minusMonths(3))
                .build();

        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(albums, filterDto));

        assertEquals(1, albums.size());
    }

}
