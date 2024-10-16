package faang.school.postservice.filter.album;

import faang.school.postservice.model.dto.AlbumFilterDto;
import faang.school.postservice.model.entity.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumAuthorFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getAuthorId() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        return albumStream.filter(album -> album.getAuthorId() == albumFilterDto.getAuthorId());
    }
}
