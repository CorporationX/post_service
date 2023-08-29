package faang.school.postservice.validator;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

@Component
public class AlbumValidator {
    public void validateOwner(UserDto user) {
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
    }

    public void validateAlbumCreation(boolean exist) {
        validateAlbumTitleUnique(exist);
    }

    public void validationOfAlbumUpdate(AlbumDto albumDto, Album albumToUpdate, boolean exist) {
        validateAuthorIdChange(albumDto, albumToUpdate);
        validateAlbumTitleUnique(exist);
    }

    private void validateAuthorIdChange(AlbumDto albumDto, Album albumToUpdate) {
        if (!albumDto.getAuthorId().equals(albumToUpdate.getAuthorId())) {
            throw new DataValidationException("AuthorId cannot be changed");
        }
    }

    private void validateAlbumTitleUnique(Boolean exist) {
        if (exist) {
            throw new DataValidationException("Title must be unique");
        }
    }
}
