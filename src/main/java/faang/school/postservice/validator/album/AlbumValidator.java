package faang.school.postservice.validator.album;

import faang.school.postservice.exception.album.AlbumException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumValidator {
private final AlbumRepository albumRepository;
private final PostRepository postRepository;

    public Album addPostToAlbumValidateService(long userId, long albumId, long postId) {
        Album foundAlbum = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumException("There is no album with such id"));

        if (userId != foundAlbum.getAuthorId()) {
            throw new AlbumException("You can add posts only in your albums");
        }

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AlbumException("There is no post with such id"));

        return foundAlbum;
    }
}
