package faang.school.postservice.service;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.publisher.AlbumCreatedEventPublisher;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumCreatedEventPublisher albumCreatedEventPublisher;

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        validateAlbum(albumDto);
        Stream<Album> albumStream = albumRepository.findByAuthorId(albumDto.getAuthorId());
        validateTitleAlbum(albumStream, albumDto);
        Album album = albumRepository.save(albumMapper.toEntity(albumDto));
        albumRepository.save(album);
        albumCreatedEventPublisher.publish(albumDto);
        return albumMapper.toDto(album);
    }


    private void validateAlbum(AlbumDto albumDto) {
        if (albumDto.getTitle() == null)
            throw new DataValidationException("Invalid title");
    }

    private void validateTitleAlbum(Stream<Album> albumStream, AlbumDto albumDto) {
        if (albumStream.filter(album -> album.getAuthorId() == albumDto.getAuthorId())
                .anyMatch(album -> album.getTitle().equals(albumDto.getTitle()))) {
            throw new DataValidationException("This album name already exists");
        }
    }
}