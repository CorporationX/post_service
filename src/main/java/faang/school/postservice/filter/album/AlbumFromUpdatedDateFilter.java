package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class AlbumFromUpdatedDateFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        return filter.getFromUpdatedDatePattern() != null;
    }

    @Override
    public Stream<AlbumUpdateDto> apply(Stream<AlbumUpdateDto> albums, AlbumFilterDto filter) {
        LocalDateTime filterDate = filter.getFromUpdatedDatePattern().atStartOfDay();

        return albums.filter(album -> !album.getUpdatedAt().isBefore(filterDate));
    }
}
