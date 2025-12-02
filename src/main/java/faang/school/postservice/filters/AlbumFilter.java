package faang.school.postservice.filters;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilter {

    boolean isApplicable(AlbumFilterDto albumFilterDto);

    Stream<Album> apply(Stream<Album> albums, AlbumFilterDto  albumFilterDto);
}
