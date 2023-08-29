package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class AlbumTitleFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getTitle() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albums.filter(album -> album.getTitle().toLowerCase().contains(albumFilterDto.getTitle().toLowerCase()));
    }
}
