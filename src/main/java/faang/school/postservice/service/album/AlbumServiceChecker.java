package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.BadRequestException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.album.Album;
import faang.school.postservice.model.album.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.postservice.model.album.AlbumVisibility.CHOSEN_USERS;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.ALBUM_NOT_EXISTS;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.TITLE_NOT_UNIQUE;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.USER_IS_NOT_CREATOR;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlbumServiceChecker {
    private final PostRepository postRepository;
    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    public boolean isExistingPosts(long postId) {
        return postRepository.existsById(postId);
    }

    public void checkUserExists(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
    }

    public Album findByIdWithPosts(long id) {
        return albumRepository.findByIdWithPosts(id)
                .orElseThrow(() -> {
                    log.error("The album with id {} not exist", id);
                    return new BadRequestException(ALBUM_NOT_EXISTS, id);
                });
    }

    public void checkAlbumExistsWithTitle(String title, long authorId) {
        boolean existsWithTitle = albumRepository.existsByTitleAndAuthorId(title, authorId);
        if (existsWithTitle) {
            log.error("The user with id {} already has album with title - {}", authorId, title);
            throw new BadRequestException(TITLE_NOT_UNIQUE, authorId, title);
        }
    }

    public void isCreatorOfAlbum(long userId, Album album) {
        if (userId != album.getAuthorId()) {
            log.error("User with id {} is not a creator of album with id {}", userId, album.getId());
            throw new BadRequestException(USER_IS_NOT_CREATOR, userId, album.getId());
        }
    }

    public void checkFavoritesAlbumsContainsAlbum(long userId, Album album, String exceptionMassage, boolean isContains) {
        List<Album> favoritesAlbums = albumRepository.findFavoriteAlbumsByUserId(userId).toList();
        if (favoritesAlbums.contains(album) == isContains) {
            log.error(exceptionMassage);
            throw new BadRequestException(exceptionMassage);
        }
    }

    public void validateAlbumVisibility(AlbumVisibility albumVisibility, List<Long> chosenUserIds) {
        if (albumVisibility == CHOSEN_USERS && chosenUserIds == null) {
            throw new BadRequestException("You have to add users when CHOSEN_USERS visibility");
        }
        if (albumVisibility != CHOSEN_USERS && chosenUserIds != null) {
            throw new BadRequestException("You cannot add users to not CHOSEN_USERS visibility");
        }
    }
}
