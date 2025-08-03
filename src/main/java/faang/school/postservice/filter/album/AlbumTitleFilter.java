package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumTitleFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        return filter.getTitlePattern() != null && !filter.getTitlePattern().isBlank();

    }

    @Override
    public Stream<AlbumUpdateDto> apply(Stream<AlbumUpdateDto> albums, AlbumFilterDto filter) {
        return albums.filter(album -> album.getTitle().equalsIgnoreCase(filter.getTitlePattern()));
    }
}
