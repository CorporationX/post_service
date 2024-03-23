package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlbumTitleFilter implements AlbumFilter{

    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getTitle() != null;
    }

    @Override
    public void apply(List<Album> albums, AlbumFilterDto albumFilterDto) {
        albums.removeIf(album -> !album.getTitle().contains(albumFilterDto.getTitle()));
    }
}
