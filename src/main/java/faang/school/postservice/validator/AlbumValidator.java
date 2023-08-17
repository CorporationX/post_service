package faang.school.postservice.validator;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;

@Component
public class AlbumValidator {
    private static final int MAX_TITLE_LENGTH = 256;
    private static final int MAX_DESCRIPTION_LENGTH = 4096;

    public void validateOwner(UserDto user) {
        if (user == null) {
            throw new NotFoundException("User not found");
        }
    }

    public void validateAlbumCreation(AlbumDto album, boolean exist) {
        validateAlbumTitleUnique(exist);
        validateAlbumTitleLength(album);
        validateAlbumDescriptionLength(album);
    }

    public void validationOfAlbumUpdate(AlbumDto albumDto, Album albumToUpdate, boolean exist) {
        validateAlbum(albumToUpdate);
        validateAuthorIdChange(albumDto, albumToUpdate);
        validateAlbumTitleUnique(exist);
        validateAlbumTitleLength(albumDto);
    }

    private void validateAuthorIdChange(AlbumDto albumDto, Album albumToUpdate) {
        if (!albumDto.getAuthorId().equals(albumToUpdate.getAuthorId())) {
            throw new IllegalArgumentException("AuthorId cannot be changed");
        }
    }

    public void validateAlbum(Album album) {
        if (album == null) {
            throw new EntityNotFoundException("Album not found");
        }
    }

    public void vaidateExistsInFavorites(boolean exist) {
        if (exist) {
            throw new IllegalArgumentException("Album already in favorites");
        }
    }

    private void validateAlbumTitleUnique(Boolean exist) {
        if (exist) {
            throw new IllegalArgumentException("Title must be unique");
        }
    }

    private void validateAlbumDescriptionLength(AlbumDto album) {
        if (album.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Description must be less than 4096 characters");
        }
    }

    private void validateAlbumTitleLength(AlbumDto album) {
        if (album.getTitle().length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Title must be less than 256 characters");
        }
    }
}
