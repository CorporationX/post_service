package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.AlbumDto;;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final UserServiceClient userServiceClient;

    private final UserContext userContext;

    public AlbumDto getAlbum(long albumId) {
        Album album = albumRepository.findById(albumId)
            .orElseThrow(() -> new EntityNotFoundException("Album with id %s not found".formatted(albumId)));

        if (userAlbumPermission(album)) {
            return albumMapper.toDto(album);
        }

        throw new IllegalArgumentException("User with id %s dont have permission to album id %s"
            .formatted(userContext.getUserId(), albumId));
    }

    public List<AlbumDto> getAllAlbums() {
        return albumRepository.findAll().stream()
            .filter(this::userAlbumPermission)
            .map(albumMapper::toDto).toList();
    }

    public AlbumDto update(AlbumDto albumDto) {
        Album album = getAlbumById(albumDto.getId());

        albumMapper.update(albumDto, album);

        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDto updateVisibility(Long albumId, AlbumVisibility visibility, @Nullable List<Long> userIds) {
        Album album = getAlbumById(albumId);
        album.setVisibility(visibility);
        if (visibility.equals(AlbumVisibility.SELECTED_USERS) && userIds != null) {
            album.setFavouriteUserIds(userIds);
        }
        return albumMapper.toDto(albumRepository.save(album));
    }

    private Album getAlbumById(Long albumId) {
        return albumRepository.findById(albumId)
            .orElseThrow(() -> new EntityNotFoundException("Album with id %s not found".formatted(albumId)));
    }

    private boolean userAlbumPermission(Album album) {
        if (userContext.getUserId() != null && userContext.getUserId().equals(album.getAuthorId())) {
            return true;
        }
        if (album.getVisibility().equals(AlbumVisibility.SELECTED_USERS) && userContext.getUserId() != null) {
            return album.getFavouriteUserIds().contains(userContext.getUserId());
        }
        if (album.getVisibility().equals(AlbumVisibility.SUBSCRIBERS) && userContext.getUserId() != null) {
            return userServiceClient.getFollowers(album.getAuthorId()).stream()
                .anyMatch(subscriptionUserDto -> subscriptionUserDto.id().equals(userContext.getUserId()));
        }

        return album.getVisibility().equals(AlbumVisibility.PUBLIC);
    }
}
