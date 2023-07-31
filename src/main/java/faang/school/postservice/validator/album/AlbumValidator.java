package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.AlbumDataValidationException;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumValidator {
    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    public void validateAlbumController(AlbumDto albumDto) {
        if (albumDto.getTitle().isEmpty() || albumDto.getDescription().isEmpty()) {
            throw new AlbumDataValidationException("Incorrect input data");
        }
    }

    public void validateAlbumService(AlbumDto albumDto) {
        UserDto user = userServiceClient.getUser(albumDto.getAuthorId());
        if (user == null) {
            throw new AlbumDataValidationException("There is no user with such id");
        }

        albumRepository.findByAuthorId(albumDto.getAuthorId())
                .forEach(album -> {
                    if (album.getTitle().equals(albumDto.getTitle())) {
                        throw new AlbumDataValidationException("Title of the album should be unique");
                    }
                });
    }
}
