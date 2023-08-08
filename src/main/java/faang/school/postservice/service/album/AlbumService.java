package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserContext userContext;

    public DeleteResult deleteAlbumOfCertainUser(Long albumId) {
        long userId = userContext.getUserId();
        Album album = albumRepository.findById(albumId).orElse(null);

        if (album == null) {
            return DeleteResult.NOT_FOUND;
        }

        if (album.getAuthorId() != userId) {
            return DeleteResult.NOT_AUTHORIZED;
        }

        albumRepository.deleteById(albumId);
        return DeleteResult.DELETED;
    }
}
