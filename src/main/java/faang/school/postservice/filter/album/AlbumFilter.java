package faang.school.postservice.filter.album;

import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilter {

    public boolean isApplicable(AlbumFilterDto albumFilterDto);

    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto);
}
