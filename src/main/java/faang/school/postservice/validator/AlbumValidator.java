package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    public void validateUniqueTitle(AlbumDto albumDto) {
        boolean isExisting = albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId());
        if (isExisting) {
            throw new IllegalArgumentException("Title must be unique");
        }
    }

    public void validateAlbumAuthor(long userId, Album album) {
        if (userId != album.getAuthorId()) {
            throw new IllegalArgumentException("You are not the author of the album");
        }
    }

    public void validateUser(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("The user is not in the system");
        }
    }

    public void validateChangeAuthor(Album album, AlbumDto albumDto) {
        if (albumDto.getAuthorId() != album.getAuthorId()) {
            throw new IllegalArgumentException("You can change only your album " +
                    "but you can't change author of the album");
        }
    }
}
