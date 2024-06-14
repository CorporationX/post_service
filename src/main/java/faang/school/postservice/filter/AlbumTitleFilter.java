package faang.school.postservice.filter;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AlbumTitleFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filterDto) {
        return Objects.nonNull(filterDto.getTitle());
    }

    @Override
    public void apply(List<AlbumDto> albums, AlbumFilterDto filterDto) {
        albums.removeIf(album -> !album.getTitle().contains(filterDto.getTitle()));
    }
}
