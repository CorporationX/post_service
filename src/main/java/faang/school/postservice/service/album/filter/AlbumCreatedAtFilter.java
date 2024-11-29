package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class AlbumCreatedAtFilter implements AlbumFilter {

  @Override
  public boolean isApplicable(AlbumFilterDto filters) {
    return filters.getCreatedAtFrom() != null && filters.getCreatedAtTo() != null;
  }

  @Override
  public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filters) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime from = LocalDateTime.parse(filters.getCreatedAtFrom(), formatter);
    LocalDateTime to = LocalDateTime.parse(filters.getCreatedAtTo(), formatter);
    return albums.filter(album -> album.getCreatedAt().isAfter(from) && album.getCreatedAt().isBefore(to));
  }
}
