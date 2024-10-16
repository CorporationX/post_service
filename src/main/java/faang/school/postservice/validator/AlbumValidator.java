package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AlbumValidator {
    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    public void validateUserIsAuthor(long userId, long authorId) {
        if (userId != authorId) {
            throw new DataValidationException("The user cannot modify someone else's album");
        }
    }

    @Transactional
    public void validateAlbumNotExists(String title, long userId) {
        if (albumRepository.existsByTitleAndAuthorId(title, userId)) {
            throw new DataValidationException(
                    String.format("Used id = %d already has album with name %s", userId, title));
        }
    }

    @Transactional
    public void validateUser(long userId) {
        if (userId <= 0) {
            throw new DataValidationException(String.format("User's id can't be equal %d", userId));
        }
        UserDto user = userServiceClient.getUser(userId);
        if (user == null) {
            throw new DataValidationException(String.format("The user must exist in the system, userId = %d", userId));
        }
    }
}
