package faang.school.postservice.validation.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlbumValidator {
    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    public void validateAlbumTitle(AlbumDto albumDto) {
        long authorId = albumDto.getAuthorId();
        String title = albumDto.getTitle();

        if (albumRepository.existsByTitleAndAuthorId(title, authorId)) {
            throw new DataValidationException(String.format("User with id %d already has an album with title %s",
                    authorId, title));
        }
    }

    public void validateAccessToAlbum(long userId, Album album) {
        AlbumVisibility visibility = album.getAlbumVisibility();
        long authorId = album.getAuthorId();

        if (AlbumVisibility.FOLLOWERS_ONLY.equals(visibility)) {
            try {
                Set<Long> followersIds = userServiceClient.getFollowers().stream()
                        .map(UserDto::getId)
                        .collect(Collectors.toSet());
                if (!followersIds.contains(userId)) {
                    throw new DataValidationException("User has no access to this album");
                }
            } catch (FeignException.InternalServerError exception) {
                throw new EntityNotFoundException("User hasn't been found by id: " + authorId);
            }
        }

        if (AlbumVisibility.SELECTED_USERS_ONLY.equals(visibility) && !album.getAllowedUsersIds().contains(userId)) {
            throw new DataValidationException("User has no access to this album");
        }
        if (AlbumVisibility.PRIVATE.equals(visibility) && userId != authorId) {
            throw new DataValidationException("User has no access to this private album");
        }
    }

    public void validateIfUserIsAuthor(long userId, Album album) {
        long authorId = album.getAuthorId();

        if (userId != authorId) {
            throw new DataValidationException(String.format("User with id %d isn't the author of the album with id %d",
                    userId, authorId));
        }
    }

    public void validateUpdatedAlbum(long authorId, AlbumDto albumDto) {
        if (authorId != albumDto.getAuthorId()) {
            throw new DataValidationException("Album author can't be changed");
        }
    }
}
