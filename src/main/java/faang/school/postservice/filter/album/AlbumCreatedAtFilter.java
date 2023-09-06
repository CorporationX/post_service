package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AlbumCreatedAtFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getCreatedAt() != null;
    }

    @Override
    public void apply(List<Album> albums, AlbumFilterDto albumFilterDto) {
        LocalDate filterDate = albumFilterDto.getCreatedAt();

        albums.removeIf(album -> !album.getCreatedAt().toLocalDate().isEqual(filterDate));
    }
}
