package faang.school.postservice.service.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class AlbumFilterByAfterTime implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return Objects.nonNull(albumFilterDto.getAfterThisTime());
    }

    @Override
    public List<Album> filterAlbums(List<Album> albums, AlbumFilterDto albumFilterDto) {
        LocalDateTime after = albumFilterDto.getAfterThisTime();
        return albums.stream()
                .filter(album -> album.getCreatedAt().isAfter(after))
                .toList();
    }
}
