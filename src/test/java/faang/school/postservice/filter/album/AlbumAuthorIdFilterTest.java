package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlbumAuthorIdFilterTest {

    private AlbumAuthorIdFilter albumAuthorIdFilter;

    @BeforeEach
    public void setUp() {
        albumAuthorIdFilter = new AlbumAuthorIdFilter();
    }

    @Test
    public void testIsApplicableWithPositiveAuthorId() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setAuthorId(1);
        assertTrue(albumAuthorIdFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableWithZeroAuthorId() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setAuthorId(0);
        assertFalse(albumAuthorIdFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableWithNegativeAuthorId() {
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setAuthorId(-1);
        assertFalse(albumAuthorIdFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyRemovesAlbumsWithDifferentAuthorId() {
        List<Album> albums = new ArrayList<>();
        Album album1 = new Album();
        album1.setAuthorId(1);
        albums.add(album1);
        Album album2 = new Album();
        album2.setAuthorId(2);
        albums.add(album2);
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setAuthorId(1);

        albumAuthorIdFilter.apply(albums, filterDto);

        assertEquals(1, albums.size());
        assertEquals(1, albums.get(0).getAuthorId());
    }

    @Test
    public void testApplyDoesNotRemoveAlbumsWithSameAuthorId() {
        List<Album> albums = new ArrayList<>();
        Album album1 = new Album();
        album1.setAuthorId(1);
        albums.add(album1);
        Album album2 = new Album();
        album2.setAuthorId(1);
        albums.add(album2);
        AlbumFilterDto filterDto = new AlbumFilterDto();
        filterDto.setAuthorId(1);

        albumAuthorIdFilter.apply(albums, filterDto);

        assertEquals(2, albums.size());
        assertEquals(1, albums.get(0).getAuthorId());
        assertEquals(1, albums.get(1).getAuthorId());
    }
}