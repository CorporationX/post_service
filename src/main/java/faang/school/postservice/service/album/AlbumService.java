package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.album.AlbumException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    @Transactional(readOnly = true)
    public AlbumDto getAlbum(long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumException("There is no album with id = " + id));
        return albumMapper.toDto(album);
    }
}
