package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import java.util.stream.Stream;

public interface AlbumFilter {

  boolean isApplicable(AlbumFilterDto filters);

  Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filters);

}
