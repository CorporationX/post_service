package faang.school.postservice.filter;

import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.filter.album.TitleFilter;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class AlbumTitleFilterTest {

    @InjectMocks
    TitleFilter titleFilter;

    private AlbumFilterDto albumFilterDto = new AlbumFilterDto();
    private Album firstAlbum;
    private Album secondAlbum;

    @BeforeEach
    void setUp() {
        firstAlbum = Album.builder()
                .id(1L)
                .title("Title")
                .build();
        secondAlbum = Album.builder()
                .id(2L)
                .title("Nothing")
                .build();
    }

    @Test
    void isApplicable_FilterIsNotApplicableWhenTitleIsNull() {
        albumFilterDto.setTitlePattern(null);
        assertFalse(titleFilter.isApplicable(albumFilterDto));
    }

    @Test
    void isApplicable_FilterIsNotApplicableWhenTitleIsEmpty() {
        albumFilterDto.setTitlePattern("");
        assertFalse(titleFilter.isApplicable(albumFilterDto));
    }

    @Test
    void apply_FilterByTitlePatternReturnsMatchingAlbums() {
        albumFilterDto.setTitlePattern("tit");

        List<Album> result = titleFilter
                .apply(Stream.of(firstAlbum, secondAlbum), albumFilterDto)
                .toList();

        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
    }

    @Test
    void apply_FilterByTitlePatternIgnoresCase() {
        albumFilterDto.setTitlePattern("tIt");

        List<Album> result = titleFilter
                .apply(Stream.of(firstAlbum, secondAlbum), albumFilterDto)
                .toList();

        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
    }
}
