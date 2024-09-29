package faang.school.postservice.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilter {
    boolean isApplicable(AlbumFilterDto albumFilterDto);

    Stream<Album> apply(AlbumFilterDto albumFilterDto, Stream<Album> albums);
}
