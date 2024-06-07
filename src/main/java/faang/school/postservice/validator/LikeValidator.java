package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataLikeValidation;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

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
        if (userServiceClient.getUser(likeDto.getUserId()) == null) {
            throw new DataLikeValidation("Автора с id " + likeDto.getUserId() + " не существует в системе");
        }
    }

    public void checkIsStandLikeWithIdOnComment(Comment comment, Like like) {
        if (Optional.ofNullable(comment.getLikes())
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(like1 -> like1.getUserId().equals(like.getUserId()))) {
            throw new DataLikeValidation("Лайк от пользователя с id " + like.getUserId() + " уже поставлен на комментарий с id " + comment.getId());
        }
    }

    public void checkIsStandLikeWithIdOnPost(Post post, Like like) {
        if (Optional.ofNullable(post.getLikes())
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(like1 -> like1.getUserId().equals(like.getUserId()))) {
            throw new DataLikeValidation("Лайк от пользователя с id " + like.getUserId() + " уже поставлен на пост с id " + post.getId());
        }
    }

    public void checkIsStandLikeOnComment(Like like) {
        if (like.getComment() != null) {
            throw new DataLikeValidation("Этот лайк уже стоит на комментарии.");
        }
    }

    public void checkIsStandLikeOnPost(Like like) {
        if (like.getPost() != null) {
            throw new DataLikeValidation("Этот лайк уже стоит на посте.");
        }
    }
}