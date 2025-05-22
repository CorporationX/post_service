package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final UserServiceClient userServiceClient;
    private final PostService postService;

    public void validateDto(CommentDto commentDto) {
        if (commentDto == null) {
            throw new DataValidationException("Комментарий не может быть null");
        }

        var content = commentDto.getContent();
        if (content == null || content.isBlank()) {
            throw new DataValidationException("Комментарий не может быть пустым");
        }
        if (content.length() > 4096) {
            throw new DataValidationException("Комментарий не может быть длиннее 4096 символов");
        }

        var authorId = commentDto.getAuthorId();
        if (authorId == null) {
            throw new DataValidationException("У комментария должен быть автор");
        }
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            throw new DataValidationException("Автор не существует: %s".formatted(authorId), e);
        }

        var postId = commentDto.getPostId();
        if (postId == 0L) {
            throw new DataValidationException("Комментарий должен относиться к посту");
        }

        postService.getPost(postId)
                .orElseThrow(() -> new DataValidationException("Пост с ID = %d не существует".formatted(postId)));
    }
}

