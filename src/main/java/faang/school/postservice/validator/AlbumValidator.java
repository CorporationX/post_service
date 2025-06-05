package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;
    private final AlbumMapper albumMapper;

    public void validateUniqueTitle(Album album) {
        if (albumRepository.existsByTitleAndAuthorId(album.getTitle(), album.getAuthorId())) {
            throw new DataValidationException("Альбом с таким названием уже существует.");
        }
    }

    public void validateAddPostToAlbum(Album album, long postId, long userId) {
        validateUserAndAccess(album, userId);
        checkPostExistenceInAlbum(album, postId);
    }

    private void validateUserAndAccess(Album album, long userId) {
        if (userId != album.getAuthorId()) {
            throw new DataValidationException("Только автор имеет доступ к альбому!");
        }
    }

    private void checkPostExistenceInAlbum(Album album, long postId) {
        List<Post> posts = album.getPosts();
        boolean isPostAlreadyExistInAlbum = posts.stream()
                .anyMatch(post -> post.getId() == postId);

        if (isPostAlreadyExistInAlbum) {
            throw new DataValidationException(
                    String.format("Пост с id '%d' уже существует в альбоме", postId));
        }
    }

    public void validateRemovePostFromAlbum(Album album, long postId, long userId) {
        validateUserAndAccess(album, userId);
        checkPostMissingInAlbum(album, postId);
    }

    private void checkPostMissingInAlbum(Album album, long postId) {
        boolean postMissing = album.getPosts().stream()
                .noneMatch(post -> post.getId() == postId);

        if (postMissing) {
            throw new DataValidationException(
                    String.format("Пост с id '%d' не найден в альбоме", postId));
        }
    }

    public void validateAddAlbumToFavorite(Album album, long userId) {
        validateUserAndAccess(album, userId);
        boolean albumExists = checkAlbumExistenceInFavorites(album, userId);
        if (albumExists) {
            throw new DataValidationException(String.format("Альбом с id '%d' уже есть в избранном", album.getId()));
        }
    }

    private boolean checkAlbumExistenceInFavorites(Album album, long userId) {
        return albumRepository.checkAlbumExistsInFavorites(album.getId(), userId);
    }

    public void validateRemoveAlbumFromFavorite(Album album, long userId) {
        validateUserAndAccess(album, userId);
        boolean albumExists = checkAlbumExistenceInFavorites(album, userId);
        if (!albumExists) {
            throw new DataValidationException(String.format("Альбома с id '%d' уже нет в избранном", album.getId()));
        }
    }

    public void validateUpdateAlbum(Album album, long userId, AlbumDto albumDto) {
        validateUserAndAccess(album, userId);

        Album updatedAlbum = albumMapper.toAlbum(albumDto);

        if (!album.getTitle().equals(updatedAlbum.getTitle())) {
            validateAlbumTitleIsUnique(userId, updatedAlbum);
        }
    }

    public void validateAlbumTitleIsUnique(long userId, Album album) {
        List<Album> albums = albumRepository.findByAuthorId(userId).toList();
        boolean isTitleNotUnique = albums.stream()
                .anyMatch(existingAlbum -> existingAlbum.getTitle().equals(album.getTitle()));

        if (isTitleNotUnique) {
            throw new DataValidationException(
                    String.format("Альбом с названием '%s' уже существует", album.getTitle()));
        }
    }

    public void validateDeleteAlbum(Album album, long userId) {
        validateUserAndAccess(album, userId);
    }
}
