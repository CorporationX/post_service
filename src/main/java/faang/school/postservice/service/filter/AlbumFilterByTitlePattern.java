package faang.school.postservice.service.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AlbumFilterByTitlePattern implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return Objects.nonNull(albumFilterDto.getTitlePattern());
    }

    @Override
    public List<Album> filterAlbums(List<Album> albums, AlbumFilterDto albumFilterDto) {
        String titlePattern = albumFilterDto.getTitlePattern().toLowerCase();

        return albums.stream()
                .filter(album -> album.getTitle().toLowerCase().contains(titlePattern))
                .toList();
    }
}
