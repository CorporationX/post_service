package faang.school.postservice.filters;

import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumTitleFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto dto) {
        return dto.title() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto dto) {
        return albums.filter(album -> album.getTitle().equals(dto.title()));
    }


}