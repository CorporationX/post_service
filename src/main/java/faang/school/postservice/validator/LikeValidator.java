package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataLikeValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final UserServiceClient userServiceClient;

    public void checkIsNull(Long id, LikeDto likeDto) {
        if (id == null) {
            throw new DataLikeValidation("Аргумент id не может быть null.");
        }
        if (likeDto == null) {
            throw new DataLikeValidation("Аргумент likeDto не может быть null.");
        }
    }

    public void checkExistAuthor(LikeDto likeDto) {
        if(userServiceClient.getUser(likeDto.getUserId()) == null) {
            throw new DataLikeValidation("Автора с id " + likeDto.getUserId() + " не существует в системе");
    }
}
}