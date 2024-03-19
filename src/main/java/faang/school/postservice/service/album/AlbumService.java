package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validation.album.AlbumValidator;
import faang.school.postservice.validation.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumValidator albumValidator;
    private final UserValidator userValidator;

    public AlbumDto create(AlbumDto albumDto) {
        userValidator.validateUserExist(albumDto.getAuthorId());
        albumValidator.validateAlbumTitle(albumDto);

        Album savedAlbum = albumRepository.save(albumMapper.toEntity(albumDto));
        return albumMapper.toDto(savedAlbum);
    }
}
