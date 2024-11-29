package faang.school.postservice.service.album.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class AlbumCreatedAtFilterTest {

  private final AlbumCreatedAtFilter albumCreatedAtFilter = new AlbumCreatedAtFilter();

  @Test
  void isApplicable() {
    boolean result = albumCreatedAtFilter.isApplicable(getTestAlbumFilterDto());
    assertTrue(result);
  }

  @Test
  void isApplicableNegative() {
    boolean result = albumCreatedAtFilter.isApplicable(getTestAlbumFilterDtoNegative());
    assertFalse(result);
  }

  @Test
  void apply() {
    Stream<Album> albums = getTestAlbumStream();
    Long expected = getIdsSum(getTestAlbumStreamFiltered());

    Stream<Album> filteredAlbums = albumCreatedAtFilter.apply(albums, getTestAlbumFilterDto());
    Long result = getIdsSum(filteredAlbums);

    assertEquals(expected, result);

  }

  private AlbumFilterDto getTestAlbumFilterDto() {
    return AlbumFilterDto.builder()
        .createdAtFrom("2024-11-21 00:00:00")
        .createdAtTo("2024-11-22 23:00:00")
        .build();
  }

  private AlbumFilterDto getTestAlbumFilterDtoNegative() {
    return AlbumFilterDto.builder()
        .authorId(1L)
        .titlePattern("some title")
        .build();
  }

  private Long getIdsSum(Stream<Album> albums) {
    return albums.map(Album::getId).reduce(0L, Long::sum);
  }

  private Stream<Album> getTestAlbumStream() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return Stream.of(
        Album.builder()
            .id(1L)
            .title("album 1")
            .description("description 1")
            .createdAt(LocalDateTime.parse("2024-11-20 00:21:39", formatter))
            .build(),
        Album.builder()
            .id(2L)
            .title("album 2")
            .description("description 2")
            .createdAt(LocalDateTime.parse("2024-11-21 00:21:39", formatter))
            .build(),
        Album.builder()
            .id(3L)
            .title("album 3")
            .description("description 3")
            .createdAt(LocalDateTime.parse("2024-11-22 00:21:39", formatter))
            .build(),
        Album.builder()
            .id(4L)
            .title("album 4")
            .description("description 4")
            .createdAt(LocalDateTime.parse("2024-11-11 00:21:39", formatter))
            .build(),
        Album.builder()
            .id(5L)
            .title("album 5")
            .description("description 5")
            .createdAt(LocalDateTime.parse("2024-11-29 00:21:39", formatter))
            .build()
    );
  }

  private Stream<Album> getTestAlbumStreamFiltered() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return Stream.of(
        Album.builder()
            .id(2L)
            .title("album 2")
            .description("description 2")
            .createdAt(LocalDateTime.parse("2024-11-21 00:21:39", formatter))
            .build(),
        Album.builder()
            .id(3L)
            .title("album 3")
            .description("description 3")
            .createdAt(LocalDateTime.parse("2024-11-22 00:21:39", formatter))
            .build());
  }
}