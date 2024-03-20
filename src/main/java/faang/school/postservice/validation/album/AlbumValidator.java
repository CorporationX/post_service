package faang.school.postservice.validation.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumValidator {
    private final AlbumRepository albumRepository;

    public void validateAlbumTitle(AlbumDto albumDto) {
        long authorId = albumDto.getAuthorId();
        String title = albumDto.getTitle();

        if (albumRepository.existsByTitleAndAuthorId(title, authorId)) {
            throw new DataValidationException(String.format("User with id %d already has an album with title %s",
                    authorId, title));
        }
    }

    public void validateIfUserIsAuthor(long userId, Album album) {
        long authorId = album.getAuthorId();

        if (userId != authorId) {
            throw new DataValidationException(String.format("User with id %d is not an author of the album with id %d",
                    userId, authorId));
        }
    }

    public void validateUpdatedAlbum(long authorId, AlbumDto albumDto) {
        if (authorId != albumDto.getAuthorId()) {
            throw new DataValidationException("Album author can't be changed");
        }
    }
}
