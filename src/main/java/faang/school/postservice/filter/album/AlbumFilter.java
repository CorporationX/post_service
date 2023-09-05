package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilter {
    boolean isApplicable(AlbumFilterDto albumFilterDto);

    Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto);
}
