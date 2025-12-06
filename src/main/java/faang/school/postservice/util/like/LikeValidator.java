package faang.school.postservice.util.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class LikeValidator {

    public void validateLike(long id, LikeDto likeDto) {
        if (likeDto.postId() == null && likeDto.commentId() == null) {
            throw new DataValidationException(
                    String.format("User #%d is trying to add like but Post and Comment do not exist",
                            likeDto.userId())
            );
        }
        if (likeDto.postId() != null && likeDto.commentId() != null) {
            throw new DataValidationException(
                    String.format("User #%d is trying to add like to Post #%d and to Comment #%d at the same time",
                            likeDto.userId(),
                            likeDto.postId(),
                            likeDto.commentId())
            );
        }
        if (likeDto.postId() != null && likeDto.postId() != id
                || likeDto.commentId() != null && likeDto.commentId() != id) {
            throw new DataValidationException(
                    String.format("User #%d is trying to add like to Post or Comment with incorrect ID #%d",
                            likeDto.userId(),
                            id)
            );
        }
    }

    public void validateUser(UserDto userDto, LikeDto likeDto) {
        if (!Objects.equals(userDto.id(), likeDto.userId())) {
            throw new DataValidationException(
                    String.format("Request includes two different User's IDs: #%d and #%d",
                            userDto.id(),
                            likeDto.userId())
            );
        }
    }
}
