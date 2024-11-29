package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class AlbumTitleFilter implements AlbumFilter {

  @Override
  public boolean isApplicable(AlbumFilterDto filters) {
    return filters.getTitlePattern() != null;
  }

  @Override
  public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filters) {
    return albums.filter(album -> album.getTitle().contains(filters.getTitlePattern()));
  }
}
