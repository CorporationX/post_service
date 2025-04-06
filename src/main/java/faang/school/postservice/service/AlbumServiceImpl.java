package faang.school.postservice.service;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.filter.AlbumSpecifications;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumSpecifications albumSpecifications;
    private final AlbumMapper albumMapper;

    @Override
    public AlbumDto create(AlbumDto albumDto) {
        if (albumRepository.existsByAuthorIdAndTitle(albumDto.authorId(), albumDto.title())) {
            throw new DataValidationException("The album already exists");
        }
        if (albumDto.description().isEmpty()) {
            throw new DataValidationException("the description is empty");
        }
        return albumMapper.toAlbumDto(albumRepository.save(albumMapper.toAlbum(albumDto)));
    }

    @Override
    public boolean existsById(long id) {
        return albumRepository.existsById(id);
    }

    @Override
    public List<Album> findWithFilter(AlbumFilterDto dto) {
        return albumRepository.findAll(albumSpecifications.getSpecification(dto));
    }
}