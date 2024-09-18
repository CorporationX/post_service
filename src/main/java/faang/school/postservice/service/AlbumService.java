package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.filter.AlbumFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final List<AlbumFilter> albumFilters;

    @Transactional
    public Album createAlbum(Album album) {
        validUserExist(album.getAuthorId());
        validUniqueAlbumTitleByAuthor(album);
        Album newAlbum = albumRepository.save(album);
        log.info("User '{}' create new album '{}'", album.getAuthorId(), album.getTitle());
        return newAlbum;
    }

    @Transactional
    public Album addPostToAlbum(long postId, long albumId, long userId) {
        validUserExist(userId);
        validAlbumBelongsToUser(albumId, userId);
        Post post = postRepository.findById(postId).orElseThrow();
        Album album = albumRepository.findById(albumId).orElseThrow();
        album.addPost(post);

        Album newAlbum = albumRepository.save(album);
        log.info("User '{}' add post '{}' to album '{}'", newAlbum.getAuthorId(), postId, album.getTitle());
        return newAlbum;
    }

    @Transactional
    public Album removePostFromAlbum(long postId, long albumId, long userId) {
        validUserExist(userId);
        validAlbumBelongsToUser(albumId, userId);
        Album album = albumRepository.findById(albumId).orElseThrow();
        album.removePost(postId);
        Album newAlbum = albumRepository.save(album);
        log.info("User '{}' delete post '{}' from album '{}'", newAlbum.getAuthorId(), postId, album.getTitle());
        return newAlbum;
    }

    @Transactional
    public void addAlbumToFavorite(long albumId, long userId) {
        validUserExist(userId);
        validAlbumExist(albumId);
        albumRepository.addAlbumToFavorites(albumId, userId);
        log.info("User '{}' add album '{}' to favorite", userId, albumId);
    }

    @Transactional
    public void removeAlbumFromFavorite(long albumId, long userId) {
        validUserExist(userId);
        validFavoriteContainsAlbum(albumId, userId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
        log.info("User '{}' remove album '{}' from favorite", userId, albumId);
    }

    @Transactional(readOnly = true)
    public Album getAlbum(long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow();
    }

    @Transactional
    public Album updateAlbum(long albumId, long authorId, Album album) {
        validAlbumBelongsToUser(albumId, authorId);
        Album existingAlbum = albumRepository.findById(albumId).orElseThrow();
        existingAlbum.setTitle(album.getTitle());
        existingAlbum.setDescription(album.getDescription());
        return albumRepository.save(existingAlbum);
    }

    @Transactional
    public void deleteAlbum(long albumId, long userId) {
        validUserExist(userId);
        validAlbumExist(albumId);
        validAlbumBelongsToUser(albumId, userId);
        albumRepository.deleteById(albumId);
    }

    @Transactional(readOnly = true)
    public List<Album> getAlbumsByFilter(AlbumFilterDto albumFilterDto) {
        List<Album> albums = albumRepository.findAll();
        return applyAllFilters(albums, albumFilterDto);
    }

    @Transactional(readOnly = true)
    public List<Album> getUserAlbumsByFilters(Long userId, AlbumFilterDto filterDto) {
        List<Album> userAlbums = albumRepository.findByAuthorId(userId).toList();
        return applyAllFilters(userAlbums, filterDto);
    }

    @Transactional(readOnly = true)
    public List<Album> getFavoriteUserAlbumsByFilters(Long userId, AlbumFilterDto filterDto) {
        List<Album> favoriteAlbumsByUser = albumRepository.findFavoriteAlbumsByUserId(userId).toList();
        return applyAllFilters(favoriteAlbumsByUser, filterDto);
    }

    private void validAlbumBelongsToUser(long albumId, long userId) {
        List<Long> userAlbumIds = albumRepository.findByAuthorId(userId)
                .map(Album::getId)
                .toList();
        if (!userAlbumIds.contains(albumId)) {
            throw new IllegalArgumentException("This album does not belong to the user.");
        }
    }

    private void validUserExist(Long authorId) {
//        UserDto userDto = userServiceClient.getUser(authorId);
//        if (Objects.isNull(userDto)) {
//            throw new IllegalArgumentException("This user does not exist.");
//        }
    }

    private void validUniqueAlbumTitleByAuthor(Album album) {
        boolean uniqueAlbumTitle = albumRepository
                .findByAuthorId(album.getAuthorId())
                .noneMatch(existingAlbum -> existingAlbum.getTitle().equals(album.getTitle()));
        if (!uniqueAlbumTitle) {
            throw new IllegalArgumentException("The album name must be unique for this user.");
        }
    }

    private List<Album> applyAllFilters(List<Album> albums, AlbumFilterDto albumFilterDto) {
        return albumFilters.stream()
                .filter(albumFilter -> albumFilter.isApplicable(albumFilterDto))
                .reduce(albums,
                        (list, filter) -> filter.filterAlbums(list, albumFilterDto),
                        (list, filter) -> list);
    }

    private void validFavoriteContainsAlbum(long albumId, long userId) {
        List<Long> userFavoriteAlbumIds = albumRepository.findFavoriteAlbumsByUserId(userId)
                .map(Album::getId)
                .toList();
        if (!userFavoriteAlbumIds.contains(albumId)) {
            throw new IllegalArgumentException("This album is not in the favorites list for this user");
        }
    }

    private void validAlbumExist(long albumId) {
        boolean existStatus = albumRepository.existsById(albumId);
        if(!existStatus){
            throw new IllegalArgumentException("Album doesn't exist.");
        }
    }
}