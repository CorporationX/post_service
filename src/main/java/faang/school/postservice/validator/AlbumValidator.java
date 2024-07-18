package faang.school.postservice.validator;

import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlbumValidator {
    private final AlbumRepository albumRepository;

    public void validateAlbumTitleDoesNotDuplicatePerAuthor(long authorId, String albumTitle) {
        boolean doesAuthorDuplicateTitle = albumRepository.existsByTitleAndAuthorId(albumTitle, authorId);
        if (doesAuthorDuplicateTitle) {
            String errMessage = String.format("User with ID: %d already has album with title: %s", authorId, albumTitle);
            log.error(errMessage);
            throw new IllegalArgumentException(errMessage);
        }
    }

    public Album validateAlbumExistence(long albumId) {
        Optional<Album> albumOptional = albumRepository.findById(albumId);
        if (albumOptional.isEmpty()) {
            String errMessage = String.format("Album with ID: %d was not found in Database", albumId);
            log.error(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
        return albumOptional.get();
    }

    public void validateAlbumBelongsToAuthor(long authorId, Album album) {
        if (album.getAuthorId() != authorId) {
            String errMessage = String.format("Album with ID: %d does not belong to author with ID: %d",
                    album.getId(), authorId);
            log.error(errMessage);
            throw new IllegalArgumentException(errMessage);
        }
    }
}