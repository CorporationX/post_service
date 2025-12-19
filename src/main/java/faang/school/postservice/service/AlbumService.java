package faang.school.postservice.service;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.model.AlbumVisibility;

import java.util.List;

public interface AlbumService {
    AlbumDto getAlbum(long albumId);

    List<AlbumDto> getAllAlbums();

    AlbumDto update(AlbumDto albumDto);

    AlbumDto updateVisibility(Long albumId, AlbumVisibility visibility, List<Long> userIds);
}