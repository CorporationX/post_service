package faang.school.postservice.filter.album;


import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumTitleFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getTitlePattern() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filters) {
        return albums.filter(album -> album.getTitle().contains(filters.getTitlePattern()));
    }
}