package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserContext userContext;

    @Transactional
    public DeleteResult deleteAlbumOfCertainUser(Long albumId) {
        long userId = userContext.getUserId();
        Album album = albumRepository.findById(albumId).orElse(null);

        if (album == null) {
            return DeleteResult.NOT_FOUND;
        }

        if (album.getAuthorId() != userId) {
            return DeleteResult.NOT_AUTHORIZED;
        }

        log.info("Deleting album with id: {}", albumId);
        albumRepository.deleteById(albumId);
        return DeleteResult.DELETED;
    }
}
