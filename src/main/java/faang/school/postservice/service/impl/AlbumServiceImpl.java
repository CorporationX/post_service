package faang.school.postservice.service.impl;

import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

private final AlbumRepository albumRepository;
private final AlbumMapper albumMapper;

    @Override
    public Album getAlbum(long id) {
        var album = albumRepository.findById(id);
        return album;
    }
}
