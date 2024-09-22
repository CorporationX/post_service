package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumCreatedBeforeFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getCreatedBefore() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        return albumStream.filter(album -> !album.getCreatedAt().isAfter(albumFilterDto.getCreatedBefore()));
    }
}
