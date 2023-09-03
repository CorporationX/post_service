package faang.school.postservice.validator;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

@Component
public class AlbumValidator {
    public void validationOfAlbumUpdate(AlbumDto albumDto, Album albumToUpdate, boolean exist) {
        validateAuthorIdChange(albumDto, albumToUpdate);
        validateAlbumTitleUnique(exist);
    }

    public void validateAlbumTitleUnique(Boolean exist) {
        if (exist) {
            throw new DataValidationException("Title must be unique");
        }
    }

    private void validateAuthorIdChange(AlbumDto albumDto, Album albumToUpdate) {
        if (!albumDto.getAuthorId().equals(albumToUpdate.getAuthorId())) {
            throw new DataValidationException("AuthorId cannot be changed");
        }
    }
}
