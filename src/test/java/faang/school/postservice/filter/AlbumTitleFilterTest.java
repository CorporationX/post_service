package faang.school.postservice.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AlbumTitleFilterTest {
    private AlbumTitleFilter albumTitleFilter;
    private AlbumFilterDto albumFilterDto;

    @BeforeEach
    public void setUp() {
        albumTitleFilter = new AlbumTitleFilter();
        albumFilterDto = new AlbumFilterDto();
    }

    @Test
    public void testIsApplicableWithEmptyFilter() {
        boolean result = albumTitleFilter.isApplicable(albumFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessfully() {
        albumFilterDto.setTitlePattern("title pattern");
        boolean result = albumTitleFilter.isApplicable(albumFilterDto);

        assertTrue(result);
    }

    @Test
    public void testApplySuccessfully() {
        albumFilterDto.setTitlePattern("some title");
        Album album = new Album();
        album.setTitle("some title");
        Album anotherAlbum = new Album();
        anotherAlbum.setTitle("title");

        Stream<Album> result = albumTitleFilter.apply(albumFilterDto, Stream.of(album, anotherAlbum));

        List<Album> resultList = result.toList();
        assertEquals(1, resultList.size());
        assertTrue(resultList.contains(album));
        assertFalse(resultList.contains(anotherAlbum));
    }
}
