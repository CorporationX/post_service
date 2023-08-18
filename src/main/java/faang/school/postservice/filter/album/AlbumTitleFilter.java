package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

@Component
public class AlbumTitleFilter extends AlbumFilter {
    @Override
    protected boolean applyFilter(Album album, AlbumFilterDto albumFilterDto) {
        return album.getTitle().contains(albumFilterDto.getTitlePattern());
    }

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getTitlePattern() != null;
    }
}