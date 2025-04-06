package faang.school.postservice.service;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;

import java.util.List;

public interface AlbumService {
    AlbumDto create(AlbumDto dto);

    boolean existsById(long l);

    List<Album> findWithFilter(AlbumFilterDto dto);
}
