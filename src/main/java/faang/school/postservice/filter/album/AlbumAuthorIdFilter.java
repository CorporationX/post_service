package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class AlbumAuthorIdFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getAuthorId() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albums.filter(album -> album.getAuthorId() == albumFilterDto.getAuthorId());
    }
}
