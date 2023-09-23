package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;

    public void validateAlbumToCreate(AlbumDto albumDto, long userId) {
        validateUser(userId);
        if (albumDto == null) {
            throw new DataValidationException("AlbumDto is null");
        }
        List<Album> albums = albumRepository.findByAuthorId(userId).toList();
        albums.forEach(album -> {
            if (album.getTitle().equals(albumDto.getTitle())) {
                throw new DataValidationException("Album with this title already exists");
            }
        });
    }

    public void validatePostToAdd(long albumId, long postId, long userId) {
        validateUser(userId);
        getAlbumFromDb(albumId);
        validateAuthor(albumId, userId);
        validatePostToExist(postId, albumId);
    }

    public void validateAuthor(long albumId, long userId) {
        validateUser(userId);
        long authorId = getAlbumFromDb(albumId).getAuthorId();
        if (authorId != userId) {
            throw new DataValidationException("You are not author of this album");
        }
    }

    public void validateFavoriteAlbumToDelete(long userId, long albumId) {
        List<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId).toList();
        if (!albums.contains(getAlbumFromDb(albumId))) {
            throw new DataValidationException("Album not in favorites");
        }
    }

    public void validatePostToExist(long postId, long albumId) {
        Post post = postService.getPostById(postId);
        Album album = getAlbumFromDb(albumId);
        List<Post> posts = album.getPosts();
        if (posts.contains(post)) {
            throw new DataValidationException("Post already in this album");
        }
    }

    public void validateUser(Long userId) {
        userServiceClient.getUser(userId);
    }

    public Album getAlbumFromDb(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new DataValidationException("Album not found"));
    }

    public void privacyCheck(Long userId, Long albumId) {
        Album album = getAlbumFromDb(albumId);

        if (album.getVisibility() == Visibility.PRIVATE && !userId.equals(album.getAuthorId())) {
            throw new DataValidationException("You are not author of this album");
        }

        if (album.getVisibility() == Visibility.ONLY_SUBSCRIBERS) {
            UserDto authorDto = userServiceClient.getUser(album.getAuthorId());
            if (!authorDto.getFollowedUserIds().contains(userId)) {
                throw new DataValidationException("You are not a subscriber of this author");
            }
        }

        if (album.getVisibility() == Visibility.ONLY_SELECTED) {
            if (!album.getAllowedUserIds().contains(userId)) {
                throw new DataValidationException("You are not allowed to see this album");
            }
        }
    }
}