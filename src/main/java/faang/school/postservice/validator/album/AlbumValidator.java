package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
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
        if (albumDto.getDescription() == null || albumDto.getDescription().isEmpty()) {
            throw new DataValidationException("Description is null");
        }
        if (albumDto.getTitle() == null || albumDto.getTitle().isEmpty()) {
            throw new DataValidationException("Title is null");
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
        validateAlbum(albumId);
        validateAuthor(albumId, userId);
        validatePostToExist(postId, albumId);
    }

    public void validateAuthor(long albumId,long userId) {
        validateUser(userId);
        long authorId = albumRepository.findById(albumId).get().getAuthorId();
        if (authorId != userId) {
            throw new DataValidationException("You are not author of this album");
        }
    }

    public void validateFavoriteAlbumToDelete(long userId,long albumId) {
        List<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId).toList();
        if(!albums.contains(albumRepository.findById(albumId).get())) {
            throw new DataValidationException("Album not in favorites");
        }
    }

    public void validatePostToExist(long postId, long albumId) {
        Post post = postService.getPostById(postId);
        Album album = albumRepository.findById(albumId).get();
        List<Post> posts = album.getPosts();
        if (posts.contains(post)) {
            throw new DataValidationException("Post already in this album");
        }
    }

    public void validateUser(Long userId) {
        userServiceClient.getUser(userId);
    }

    public void validateAlbum(Long albumId) {
        albumRepository.findById(albumId).orElseThrow(() -> new DataValidationException("Album not found"));
    }
}