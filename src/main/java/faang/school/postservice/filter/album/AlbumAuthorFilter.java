package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

public class AlbumAuthorFilter extends AlbumFilter {
    @Override
    protected boolean applyFilter(Album album, AlbumFilterDto albumFilterDto) {
        return album.getAuthorId() == albumFilterDto.getAuthorIdPattern();
    }

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getAuthorIdPattern() != null;
    }
}
