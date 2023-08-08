package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final AlbumValidator albumValidator;
    private final UserServiceClient userServiceClient;

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        UserDto user = userServiceClient.getUser(albumDto.getAuthorId());

        if (albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), user.getId())) {
            throw new IllegalArgumentException("Title must be unique");
        }

        albumValidator.validateOwner(user);
        albumValidator.validateAlbumCreation(albumDto);

        Album albumToSave = albumMapper.toAlbum(albumDto);

        return albumMapper.toDto(albumRepository.save(albumToSave));
    }

    @Transactional
    public AlbumDto updateAlbum(AlbumDto albumDto) {
        UserDto user = userServiceClient.getUser(albumDto.getAuthorId());
        albumValidator.validateOwner(user);

        Album albumToUpdate = albumRepository.findById(albumDto.getId()).orElse(null);

        albumValidator.validationOfAlbumUpdate(albumDto, albumToUpdate);

        albumMapper.updateAlbumFromDto(albumDto, albumToUpdate);

        return albumMapper.toDto(albumRepository.save(albumToUpdate));
    }

    @Transactional
    public void deleteAlbum(Long album) {
        Album albumToDelete = albumRepository.findById(album).orElse(null);
        albumValidator.validateAlbum(albumToDelete);
        albumRepository.delete(albumToDelete);
    }
}
