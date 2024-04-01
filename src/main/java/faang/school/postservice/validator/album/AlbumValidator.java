package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumValidator {

    private final UserServiceClient userServiceClient;

    public void validate(String title, String description, Long authorId) {
        if (title == null) {
            throw new DataValidationException("Не заполнено название");
        }
        if (description == null) {
            throw new DataValidationException("Не заполненл описание");
        }
        if (authorId == null) {
            throw new DataValidationException("Не заполнен пользователь");
        }
    }

    public void validate(Stream<Album> albumStream, AlbumDto albumDto) {
        if (albumStream.filter(album -> album.getAuthorId() == albumDto.getAuthorId())
                .anyMatch(album -> album.getTitle().equals(albumDto.getTitle()))) {
            throw new DataValidationException("Такое имя альбома уже есть");
        }
    }

    public void validate(Stream<Album> albumStream, AlbumUpdateDto albumUpdateDto) {
        if (albumStream.filter(album -> album.getAuthorId() == albumUpdateDto.getAuthorId())
                .anyMatch(album -> album.getTitle().equals(albumUpdateDto.getTitle()))) {
            throw new DataValidationException("Такое имя альбома уже есть");
        }
    }

    public void validate(Long userId) {
        userServiceClient.getUser(userId);
        if (userServiceClient.getUser(userId) == null) {
            throw new DataValidationException("Нет такого автора");
        }
    }


}
