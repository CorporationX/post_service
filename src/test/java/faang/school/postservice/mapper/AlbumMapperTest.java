package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AlbumMapperTest {
    @Spy
    private AlbumMapperImpl albumMapper;

    private AlbumDto albumDto;
    private Album album;

    @BeforeEach
    void setUp() {
        albumDto = AlbumDto.builder().id(1L).title("title").description("content").authorId(1L).build();
        album = Album.builder().id(1L).title("title").description("content").authorId(1L).build();
    }

    @Test
    void testAlbumToAlbumDtoShouldMatchAllFields() {
        AlbumDto actual = albumMapper.toDto(album);
        assertAll(
                () -> assertEquals(albumDto.getId(), actual.getId()),
                () -> assertEquals(albumDto.getDescription(), actual.getDescription()),
                () -> assertEquals(albumDto.getAuthorId(), actual.getAuthorId())
        );
    }

    @Test
    void testAlbumDtoToAlbumShouldMatchAllFields() {
        Album actual = albumMapper.toAlbum(albumDto);
        assertAll(
                () -> assertEquals(album.getId(), actual.getId()),
                () -> assertEquals(album.getDescription(), actual.getDescription()),
                () -> assertEquals(album.getAuthorId(), actual.getAuthorId())
        );
    }

    @Test
    void testToDto() {
        AlbumDto actual = albumMapper.toDto(album);
        assertAll(
                () -> assertEquals(albumDto.getId(), actual.getId()),
                () -> assertEquals(albumDto.getDescription(), actual.getDescription()),
                () -> assertEquals(albumDto.getAuthorId(), actual.getAuthorId())
        );
    }

    @Test
    void testToAlbum() {
        Album actual = albumMapper.toAlbum(albumDto);
        assertAll(
                () -> assertEquals(album.getId(), actual.getId()),
                () -> assertEquals(album.getDescription(), actual.getDescription()),
                () -> assertEquals(album.getAuthorId(), actual.getAuthorId())
        );
    }
}