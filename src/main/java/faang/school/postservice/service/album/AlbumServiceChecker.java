package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClientMock;
import faang.school.postservice.exception.BadRequestException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.postservice.model.AlbumVisibility.CHOSEN_USERS;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.ALBUM_NOT_EXISTS;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.TITLE_NOT_UNIQUE;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.USER_IS_NOT_CREATOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlbumServiceChecker {
    private final PostRepository postRepository;
    private final AlbumRepository albumRepository;
    private final UserServiceClientMock userServiceClient;

    public boolean isExistingPosts(long postId) {
        return postRepository.existsById(postId);
    }

    public void checkUserExists(Long userId) {
        userServiceClient.getUser(userId);
    }

    public Album findByIdWithPosts(long id) {
        return albumRepository.findByIdWithPosts(id)
            .orElseThrow(() -> {
                log.error(ALBUM_NOT_EXISTS);
                return new BadRequestException(ALBUM_NOT_EXISTS);
            });
    }

    public void checkAlbumExistsWithTitle(String title, long authorId) {
        boolean existsWithTitle = albumRepository.existsByTitleAndAuthorId(title, authorId);
        if (existsWithTitle) {
            log.error(TITLE_NOT_UNIQUE);
            throw new BadRequestException(TITLE_NOT_UNIQUE);
        }
    }

    public void isCreatorOfAlbum(long userId, Album album) {
        if (userId != album.getAuthorId()) {
            log.error(USER_IS_NOT_CREATOR);
            throw new BadRequestException(USER_IS_NOT_CREATOR);
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
