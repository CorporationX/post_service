package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.album.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumTitleFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        String title = filter.getTitle();
        return title != null && !title.isBlank();
    }

    @Override
    public Stream<Album> apply(Stream<Album> albumStream, AlbumFilterDto filter) {
        return albumStream
                .filter(album -> album.getTitle().equals(filter.getTitle()));
    }
}
