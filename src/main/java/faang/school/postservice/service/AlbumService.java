package faang.school.postservice.service;

import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.UserVisibility;
import faang.school.postservice.model.VisibilityType;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    public AlbumDto createAlbum(AlbumDto albumDto) {
        Album album = albumMapper.toEntity(albumDto);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDto getAlbum(Long albumId) {
        Long userId = userContext.getUserId();
        Optional<Album> album = albumRepository.findByIdWithPosts(albumId);
        if (userId == null || !album.isPresent()) {
            return new AlbumDto();
        } else {
            if (userId.equals(album.get().getAuthorId())) {
                return albumMapper.toDto(album.get());
            } else if (album.get().getVisibilityType().equals(VisibilityType.All_USER)) {
                return albumMapper.toDto(album.get());
            } else if (album.get().getVisibilityType().equals(VisibilityType.ONLY_SUBSCRIBERS)) {
                UserDto authorDto = userServiceClient.getUser(album.get().getAuthorId());
                Long authorId = authorDto.getFollowersId()
                        .stream()
                        .filter(follower -> follower.equals(userId))
                        .findFirst().orElse(null);
                if (authorId != null) {
                    return albumMapper.toDto(album.get());
                } else {
                    log.info("user is not follower");
                    return new AlbumDto();
                }
            } else if (album.get().getVisibilityType().equals(VisibilityType.ONLY_USERS_SELECTED)) {
                UserVisibility userVisibilityResult = album.get().getVisibilityUsers()
                        .stream()
                        .filter(userVisibility -> userVisibility.getUserId().equals(userId))
                        .findFirst()
                        .orElse(null);
                if (userVisibilityResult != null) {
                    return albumMapper.toDto(album.get());
                } else {
                    log.info("user is not follower");
                }
            }
        }
        return new AlbumDto();
    }
}