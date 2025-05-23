package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentValidator {

    private final UserServiceClient userServiceClient;
    private final PostService postService;

    public void validateDto(CommentDto commentDto) {
        if (commentDto == null) {
            throw new DataValidationException("Комментарий не может быть null");
        }

        validateContentDto(commentDto);

        isAuthorPresent(commentDto);

        validatePostDto(commentDto);
    }

    public void validatePostDto(CommentDto commentDto) {
        var postId = commentDto.getPostId();
        if (postId == 0L) {
            throw new DataValidationException("Комментарий должен относиться к посту");
        }

        postService.getPost(postId)
                .orElseThrow(() -> new DataValidationException("Пост с ID = %d не существует".formatted(postId)));
    }

    public void validateContentDto(CommentDto commentDto) {
        var content = commentDto.getContent();
        if (content == null || content.isBlank()) {
            throw new DataValidationException("Комментарий не может быть пустым");
        }
        if (content.length() > 4096) {
            throw new DataValidationException("Комментарий не может быть длиннее 4096 символов");
        }
    }

    public void isAuthorPresent(CommentDto commentDto) {
        var authorId = commentDto.getAuthorId();
        if (authorId == null) {
            throw new DataValidationException("У комментария должен быть автор");
        }
        try {
            log.info("Проверяем наличие пользователя в базе...");
            userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            throw new DataValidationException("Автор не существует: %s".formatted(authorId), e);
        }
    }

    public void validateIdDto(long id, CommentDto commentDto) {
        if (commentDto == null || commentDto.getId() == null) {
            throw new DataValidationException("Комментарий и его ID не могут быть null");
        }

        Long commentId = commentDto.getId();

        if (id <= 0 || commentId <= 0) {
            throw new DataValidationException("ID должны быть положительными числами");
        }

        if (!commentId.equals(id)) {
            throw new DataValidationException("ID в пути и в теле запроса не совпадают");
        }
    }

    public void validateCommentId(long id) {
        if (id <= 0) {
            throw new DataValidationException("ID должно быть положительным числом");
        }
    }
}

