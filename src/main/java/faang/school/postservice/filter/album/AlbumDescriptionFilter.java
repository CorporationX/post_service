package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlbumDescriptionFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getDescription() != null;
    }

    @Override
    public void apply(List<Album> albums, AlbumFilterDto albumFilterDto) {
        albums.removeIf(album -> !album.getDescription().contains(albumFilterDto.getDescription()));
    }
}
