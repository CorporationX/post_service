package faang.school.postservice.service.validator;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ServiceValidator {

    public void validateUserReal(LikeDto likeDto, UserDto userDto) {
        if (!userDto.getId().equals(likeDto.getUserId())) {
            throw new DataValidationException("User by id:" + likeDto.getUserId() + " not found in ValidateUserReal");
        }
    }

    public void validateDuplicateLikeForPost(LikeDto likeDto, List<Like> postLikes) {
        for (Like like : postLikes) {
            if (Objects.equals(like.getUserId(), likeDto.getUserId())) {
                throw new DataValidationException("Post already liked!");
            }
        }
    }

    public void validateDuplicateLikeForComment(LikeDto likeDto, List<Like> commentLikes) {
        for (Like like : commentLikes) {
            if (Objects.equals(like.getUserId(), likeDto.getUserId())) {
                throw new DataValidationException("Comment already liked!");
            }
        }
    }
}
