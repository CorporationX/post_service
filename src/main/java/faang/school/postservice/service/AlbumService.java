package faang.school.postservice.service;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumValidator validator;

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        validator.validateUniqueTitle(albumDto);
        Album saved = albumRepository.save(albumMapper.toEntity(albumDto));
        return albumMapper.toDto(saved);
    }


}
