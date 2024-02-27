package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.ProjectDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    public void validateIsNotPublished(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Пост уже опубликован");
        }
    }

    public void validateAuthorCount(PostDto postDto) {
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("У поста должен быть автор");
        }

        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("У поста должен быть только один автор");
        }
    }

    public void validateContentExists(String content) {
        if (content == null || content.isBlank()) {
            throw new DataValidationException("Пост не может быть пустым");
        }
    }

    public void validateAuthorExists(UserDto author, ProjectDto project) {
        if (author == null && project == null) {
            throw new DataValidationException("Такого автора не существует");
        }
    }

    public void validateAuthor(long authorId, long userId) {
        if (authorId != userId) {
            throw new DataValidationException("You are not the author");
        }
    }
}
