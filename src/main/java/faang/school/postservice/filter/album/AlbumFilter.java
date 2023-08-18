package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public abstract class AlbumFilter {

    public Stream<Album> applyFilter(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albums.filter(album -> applyFilter(album, albumFilterDto));
    }
    protected abstract boolean applyFilter(Album album, AlbumFilterDto albumFilterDto);

    public abstract boolean isApplicable(AlbumFilterDto albumFilterDto);
}