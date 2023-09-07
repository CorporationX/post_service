package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.List;

public interface AlbumFilter {
    boolean isApplicable(AlbumFilterDto albumFilterDto);

    void apply(List<Album> albums, AlbumFilterDto albumFilterDto);
}
