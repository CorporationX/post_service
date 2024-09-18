package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClientMock;
import faang.school.postservice.exception.BadRequestException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
                .orElseThrow(() -> new BadRequestException("The album does not exist"));
    }

    public void checkAlbumExistsWithTitle(String title, long authorId) {
        boolean existsWithTitle = albumRepository.existsByTitleAndAuthorId(title, authorId);
        if (existsWithTitle) {
            throw new BadRequestException("The album title must be unique");
        }
    }

    public void isCreatorOfAlbum(long userId, Album album) {
        if (userId != album.getAuthorId()) {
            throw new BadRequestException("The user cannot change someone else's album");
        }
    }

    public Album getAlbumAfterChecks(long userId, long albumId) {
        checkUserExists(userId);
        Album album = findByIdWithPosts(albumId);
        isCreatorOfAlbum(userId, album);
        return album;
    }
}
