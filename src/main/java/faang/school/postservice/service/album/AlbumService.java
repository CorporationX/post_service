package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        boolean existsByTitleAndAuthorId = isExistsByTitleAndAuthorId(albumDto.getTitle(), user.getId());

        albumValidator.validateOwner(user);
        albumValidator.validateAlbumCreation(existsByTitleAndAuthorId);

        Album albumToSave = albumMapper.toAlbum(albumDto);

        return albumMapper.toDto(albumRepository.save(albumToSave));
    }

    @Transactional
    public AlbumDto updateAlbum(AlbumDto albumDto) {
        UserDto user = userServiceClient.getUser(albumDto.getAuthorId());
        Album albumToUpdate = getAlbumById(albumDto.getId());
        boolean existsByTitleAndAuthorId = isExistsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId());

        albumValidator.validateOwner(user);
        albumValidator.validationOfAlbumUpdate(albumDto, albumToUpdate, existsByTitleAndAuthorId);

        Album updatedAlbum = albumMapper.toAlbum(albumDto);
        updatedAlbum.setUpdatedAt(LocalDateTime.now());

        return albumMapper.toDto(albumRepository.save(updatedAlbum));
    }

    @Transactional
    public void deleteAlbum(Long albumId) {
        albumRepository.deleteById(albumId);
    }

    private Album getAlbumById(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new EntityNotFoundException("Album not found"));
    }

    private boolean isExistsByTitleAndAuthorId(String title, Long userId) {
        return albumRepository.existsByTitleAndAuthorId(title, userId);
    }
}
