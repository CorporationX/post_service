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
public class AlbumDescriptionFilterTest {
    private AlbumDescriptionFilter albumDescriptionFilter;
    private AlbumFilterDto albumFilterDto;

    @BeforeEach
    public void setUp() {
        albumDescriptionFilter = new AlbumDescriptionFilter();
        albumFilterDto = new AlbumFilterDto();
    }

    @Test
    public void testIsApplicableWithEmptyFilter() {
        boolean result = albumDescriptionFilter.isApplicable(albumFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessfully() {
        albumFilterDto.setDescriptionPattern("description");
        boolean result = albumDescriptionFilter.isApplicable(albumFilterDto);

        assertTrue(result);
    }

    @Test
    public void testApplySuccessfully() {
        albumFilterDto.setDescriptionPattern("some description");
        Album album = new Album();
        album.setDescription("some description");
        Album anotherAlbum = new Album();
        anotherAlbum.setDescription("description");

        Stream<Album> result = albumDescriptionFilter.apply(albumFilterDto, Stream.of(album, anotherAlbum));

        List<Album> resultList = result.toList();
        assertEquals(1, resultList.size());
        assertTrue(resultList.contains(album));
        assertFalse(resultList.contains(anotherAlbum));
    }
}
