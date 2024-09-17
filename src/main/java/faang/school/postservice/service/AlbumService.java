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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final List<AlbumFilter> albumFilters;

    @Transactional
    public Album createAlbum(Album album) {
        validUserExist(album.getAuthorId());
        validUniqueAlbumTitleByAuthor(album);
        return albumRepository.save(album);
    }

    @Transactional
    public Album addPostToAlbum(long postId, long albumId, long userId) {
        validUserExist(userId);
        validAlbumBelongsToUser(albumId, userId);
        Post post = postRepository.findById(postId).orElseThrow();
        Album album = albumRepository.findById(albumId).orElseThrow();
        album.addPost(post);
        return albumRepository.save(album);
    }

    @Transactional
    public Album removePostFromAlbum(long postId, long albumId, long userId) {
        validUserExist(userId);
        validAlbumBelongsToUser(albumId, userId);
        Album album = albumRepository.findById(albumId).orElseThrow();
        album.removePost(postId);
        return albumRepository.save(album);
    }

    @Transactional
    private void validAlbumBelongsToUser(long albumId, long userId) {
        List<Long> userAlbumIds = albumRepository.findByAuthorId(userId)
                .map(album -> album.getId())
                .toList();
        if (!userAlbumIds.contains(albumId)) {
            throw new IllegalArgumentException("error");
        }
    }

    @Transactional
    public void addAlbumToFavorite(long albumId, long userId) {
        validUserExist(userId);
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Transactional
    public void removeAlbumToFavorite(long albumId, long authorId) {
        validUserExist(authorId);
        albumRepository.deleteAlbumFromFavorites(albumId, authorId);
    }

    @Transactional(readOnly = true)
    public Album getAlbum(long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<Album> getAlbumsByFilters(AlbumFilterDto albumFilterDto) {
        List<AlbumFilter> applicableAlbumFilters = albumFilters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .toList();

        Set<Album> albums = applicableAlbumFilters.stream()
                .map(filter -> new HashSet<>(filter.getAlbums(albumFilterDto)))
                .reduce((result, set) -> {
                    result.retainAll(set);
                    return result;
                })
                .orElse(new HashSet<>());

        return albums.stream().toList();
    }

    @Transactional(readOnly = true)
    public List<Album> getUserAlbumsByFilters(Long userId, AlbumFilterDto filterDto) {
        return getAlbumsByFilters(filterDto).stream()
                .filter(album -> album.getAuthorId() == userId)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Album> getFavoriteUserAlbumsByFilters(Long userId, AlbumFilterDto filterDto) {
        List<Long> userAlbumsIds = getUserAlbumsByFilters(userId, filterDto).stream()
                .map(Album::getId)
                .toList();

        return albumRepository.findFavoriteAlbumByAllIds(userAlbumsIds);
    }


    private void validUserExist(Long authorId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        if (Objects.isNull(userDto)) {
            throw new IllegalArgumentException("This user does not exist.");
        }
    }

    private void validUniqueAlbumTitleByAuthor(Album album) {
        boolean uniqueAlbumTitle = albumRepository
                .findByAuthorId(album.getAuthorId())
                .noneMatch(existingAlbum -> existingAlbum.getTitle().equals(album.getTitle()));
        if (!uniqueAlbumTitle) {
            throw new IllegalArgumentException("The album name must be unique for this user.");
        }
    }
}