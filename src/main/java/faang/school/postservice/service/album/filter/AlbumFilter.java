package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.album.Album;

import java.util.stream.Stream;

public interface AlbumFilter {
    boolean isApplicable(AlbumFilterDto filter);
    Stream<Album> apply(Stream<Album> albumStream, AlbumFilterDto filter);
}
