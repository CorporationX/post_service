package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AlbumTitleFilterTest {

    @InjectMocks
    AlbumTitleFilter albumTitleFilter;

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
    void isApplicable_FilterIsApplicable() {
        albumFilterDto.setTitle("itl");
        assertTrue(albumTitleFilter.isApplicable(albumFilterDto));
    }

    @Test
    void isApplicable_FilterIsNotApplicable() {
        assertFalse(albumTitleFilter.isApplicable(albumFilterDto));
    }

    @Test
    void apply_AlbumsAreFilteredByTitle() {
        albumFilterDto.setTitle("itl");
        List<Album> albums = new ArrayList<>(List.of(firstAlbum, secondAlbum));

        albumTitleFilter.apply(albums, albumFilterDto);

        assertAll(
                () -> assertEquals(1, albums.size()),
                () -> assertFalse(albums.contains(secondAlbum)),
                () -> assertTrue(albums.contains(firstAlbum))
        );
    }
}
