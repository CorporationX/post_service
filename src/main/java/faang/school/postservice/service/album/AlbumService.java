package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final AlbumServiceChecker checker;
    private final List<AlbumFilter> albumFilters;

    @Transactional
    public Album createNewAlbum(long authorId, Album album) {
        checker.checkUserExists(album.getAuthorId());
        checker.checkAlbumExistsWithTitle(album.getTitle(), album.getAuthorId());
        album.setAuthorId(authorId);
        return albumRepository.save(album);
    }

    @Transactional(readOnly = true)
    public Album getAlbum(long userId, long albumId) {
        checker.checkUserExists(userId);
        return checker.findByIdWithPosts(albumId);
    }

    @Transactional
    public Album updateAlbum(long userId, long albumId, String title, String description) {
        checker.checkUserExists(userId);
        Album albumToUpdate = checker.findByIdWithPosts(albumId);
        checker.isCreatorOfAlbum(userId, albumToUpdate);
        if (title != null && title.isBlank()) {
            checker.checkAlbumExistsWithTitle(title, userId);
            albumToUpdate.setTitle(title);
        }
        if (description != null && description.isBlank()) {
            albumToUpdate.setDescription(description);
        }
        return albumRepository.save(albumToUpdate);
    }

    @Transactional
    public Album deleteAlbum(long userId, long albumId) {
        checker.checkUserExists(userId);
        Album album = checker.findByIdWithPosts(albumId);
        checker.isCreatorOfAlbum(userId, album);
        albumRepository.delete(album);
        return album;
    }

    @Transactional
    public Album addAlbumToFavorites(long userId, long albumId) {
        checker.checkUserExists(userId);
        Album album = checker.findByIdWithPosts(albumId);
        checker.isCreatorOfAlbum(userId, album);
        albumRepository.addAlbumToFavorites(albumId, userId);
        return album;
    }

    @Transactional
    public Album deleteAlbumFromFavorites(long userId, long albumId) {
        checker.checkUserExists(userId);
        Album album = checker.findByIdWithPosts(albumId);
        checker.isCreatorOfAlbum(userId, album);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
        return album;
    }

    @Transactional
    public Album addNewPosts(long userId, long albumId, List<Long> postIds) {
        checker.checkUserExists(userId);
        Album album = checker.findByIdWithPosts(albumId);
        checker.isCreatorOfAlbum(userId, album);
        List<Long> existingPosts = postIds.stream()
                .filter(checker::isExistingPosts)
                .toList();
        List<Post> posts = postRepository.findAllById(existingPosts);
        posts.forEach(album::addPost);
        return albumRepository.save(album);
    }

    @Transactional
    public Album deletePosts(long userId, long albumId, List<Long> postIds) {
        checker.checkUserExists(userId);
        Album album = checker.findByIdWithPosts(albumId);
        checker.isCreatorOfAlbum(userId, album);
        postIds.forEach(album::removePost);
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
        return findAlbumsByStreamAndFilters(allAlbums, filters);
    }

    private List<Album> findAlbumsByStreamAndFilters(Stream<Album> albumStream, AlbumFilterDto filters) {
        return albumFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(albumStream, (stream, filter) -> filter
                        .apply(stream, filters), (s1, s2) -> s1)
                .toList();
    }
}

