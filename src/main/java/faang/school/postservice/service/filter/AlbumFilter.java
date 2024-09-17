package faang.school.postservice.service.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.List;

public interface AlbumFilter {
    boolean isApplicable(AlbumFilterDto albumFilterDto);

    List<Album> getAlbums(AlbumFilterDto albumFilterDto);
}
