package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClientMock;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumChosenUsers;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.ALREADY_FAVORITE;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.NOT_FAVORITE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final AlbumServiceChecker checker;
    private final List<AlbumFilter> albumFilters;
    private final UserServiceClientMock userServiceClient;

    @Transactional
    public Album createAlbum(long authorId, Album album, List<Long> chosenUserIds) {
        checker.checkUserExists(authorId);
        checker.checkAlbumExistsWithTitle(album.getTitle(), authorId);
        checker.validateAlbumVisibility(album.getVisibility(), chosenUserIds);

        album.setAuthorId(authorId);
        setChosenUsersToAlbum(album, chosenUserIds);

        log.info("Album created");
        return albumRepository.save(album);
    }

    @Transactional(readOnly = true)
    public Album getAlbum(long userId, long albumId) {
        checker.checkUserExists(userId);
        return checker.findByIdWithPosts(albumId);
    }

    @Transactional
    public Album updateAlbum(long userId, long albumId, String title, String description) {
        Album album = getAlbumAfterChecks(userId, albumId);
        if (title != null && !title.isBlank()) {
            checker.checkAlbumExistsWithTitle(title, userId);
            album.setTitle(title);
        }
        if (description != null && !description.isBlank()) {
            album.setDescription(description);
        }
        log.info("Album with id {} updated", albumId);
        return albumRepository.save(album);
    }

    @Transactional
    public Album updateAlbumVisibility(long userId, long albumId, AlbumVisibility visibility, List<Long> chosenUserIds) {
        Album album = getAlbumAfterChecks(userId, albumId);
        checker.validateAlbumVisibility(visibility, chosenUserIds);

        album.setVisibility(visibility);
        setChosenUsersToAlbum(album, chosenUserIds);

        return albumRepository.save(album);
    }

    private void setChosenUsersToAlbum(Album album, List<Long> chosenUserIds) {
        if (chosenUserIds == null) {
            album.setChosenUsers(null);
        } else {
            AlbumChosenUsers albumChosenUsers = AlbumChosenUsers.builder().userIds(chosenUserIds).build();
            album.setChosenUsers(albumChosenUsers);
        }
    }

    @Transactional
    public Album deleteAlbum(long userId, long albumId) {
        Album album = getAlbumAfterChecks(userId, albumId);
        albumRepository.delete(album);
        log.info("Album with id {} deleted", albumId);
        return album;
    }

    @Transactional
    public Album addAlbumToFavorites(long userId, long albumId) {
        checker.checkUserExists(userId);
        Album album = checker.findByIdWithPosts(albumId);
        checker.checkFavoritesAlbumsContainsAlbum(userId, album, ALREADY_FAVORITE, true);
        albumRepository.addAlbumToFavorites(albumId, userId);
        log.info("Album with id {} added to favorites albums", albumId);
        return album;
    }

    @Transactional
    public Album deleteAlbumFromFavorites(long userId, long albumId) {
        checker.checkUserExists(userId);
        Album album = checker.findByIdWithPosts(albumId);
        checker.checkFavoritesAlbumsContainsAlbum(userId, album, NOT_FAVORITE, false);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
        log.info("Album with id {} deleted from favorites albums", albumId);
        return album;
    }

    @Transactional
    public Album addNewPosts(long userId, long albumId, List<Long> postIds) {
        Album album = getAlbumAfterChecks(userId, albumId);
        List<Long> existingPosts = postIds.stream()
            .filter(checker::isExistingPosts)
            .toList();
        List<Post> posts = postRepository.findAllById(existingPosts);
        posts.forEach(album::addPost);
        log.info("Posts added to album with id {}", albumId);
        return albumRepository.save(album);
    }

    @Transactional
    public Album deletePosts(long userId, long albumId, List<Long> postIds) {
        Album album = getAlbumAfterChecks(userId, albumId);
        postIds.forEach(album::removePost);
        log.info("Posts deleted from album with id {}", albumId);
        return albumRepository.save(album);
    }

    @Transactional(readOnly = true)
    public List<Album> getUserAlbums(long userId, AlbumFilterDto filters) {
        checker.checkUserExists(userId);
        Stream<Album> userAlbums = albumRepository.findByAuthorId(userId).stream();
        return findAlbumsByStreamAndFilters(userAlbums, filters);
    }

    @Transactional(readOnly = true)
    public List<Album> getFavoriteAlbums(long userId, AlbumFilterDto filters) {
        checker.checkUserExists(userId);
        Stream<Album> favoriteAlbums = albumRepository.findFavoriteAlbumsByUserId(userId);
        return findAlbumsByStreamAndFilters(favoriteAlbums, filters);
    }

    @Transactional(readOnly = true)
    public List<Album> getAllAlbums(long userId, AlbumFilterDto filters) {
        checker.checkUserExists(userId);
        Stream<Album> allAlbums = albumRepository.findAll().stream();
        Stream<Album> visibleAlbums = allAlbums.filter(isVisibleForUser(userId));
        return findAlbumsByStreamAndFilters(visibleAlbums, filters);
    }

    public Album getAlbumAfterChecks(long userId, long albumId) {
        checker.checkUserExists(userId);
        Album album = checker.findByIdWithPosts(albumId);
        checker.isCreatorOfAlbum(userId, album);
        return album;
    }

    private List<Album> findAlbumsByStreamAndFilters(Stream<Album> albumStream, AlbumFilterDto filters) {
        return albumFilters.stream()
            .filter(filter -> filter.isApplicable(filters))
            .reduce(albumStream, (stream, filter) -> filter
                .apply(stream, filters), (s1, s2) -> s1)
            .peek(album -> log.info("Album find: {}", album.getId()))
            .toList();
    }

    private Predicate<Album> isVisibleForUser(long userId) {
        return album -> isAlbumVisibleForUser(album, userId);
    }

    private boolean isAlbumVisibleForUser(Album album, long userId) {
        long authorId = album.getAuthorId();
        if (album.getAuthorId() == userId) {
            return true;
        }

        AlbumVisibility visibility = album.getVisibility();
        return switch (visibility) {
            case ALL_USERS -> true;
            case SUBSCRIBERS_ONLY -> userServiceClient.getFollowers(authorId).contains(userId);
            case CHOSEN_USERS -> album.getChosenUsers().getUserIds().contains(userId);
            case AUTHOR_ONLY -> false;
        };
    }
}

