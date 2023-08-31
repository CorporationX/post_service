package faang.school.postservice.filter.album_filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.album.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumCreatedAtFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getCreatedAt() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albums.filter(album -> album.getCreatedAt().isEqual(albumFilterDto.getCreatedAt()));
    }
}
