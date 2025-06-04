package faang.school.postservice.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumTitlePatternFilter implements AlbumFilter {

    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getTitlePattern() != null;
    }

    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albums.filter(album -> albumFilterDto.getTitlePattern().equalsIgnoreCase(album.getTitle()));
    }
}