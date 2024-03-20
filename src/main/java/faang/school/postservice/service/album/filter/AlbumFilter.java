package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.List;

public interface AlbumFilter {

    boolean isApplicable(AlbumFilterDto filters);

    void apply(List<Album> albums, AlbumFilterDto albumFilterDto);
}
