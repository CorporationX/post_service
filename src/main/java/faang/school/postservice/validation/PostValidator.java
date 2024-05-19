package faang.school.postservice.validation;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostValidator {

    public void validateAuthorCount(PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("Пост должен иметь автора");
        }
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("Пост не может иметь два автора");
        }
    }

    public void validateAuthorExist(UserDto author, ProjectDto project) {
        if (author == null && project == null) {
            throw new DataValidationException("Такого автора не существует");
        }
    }

    public void validateIsNotPublished(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Пост уже опубликован");
        }
    }

    public void validateChangeAuthor(Post post, PostDto postDto) {
        if (post.getAuthorId() != null && !post.getAuthorId().equals(postDto.getAuthorId())) {
            throw new DataValidationException("Нельзя изменить автора поста");
        }

        if (post.getProjectId() != null && !post.getProjectId().equals(postDto.getProjectId())) {
            throw new DataValidationException("Нельзя изменить автора поста");
        }
    }

    public void validatePostsExists(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            throw new EntityNotFoundException("Posts not found");
        }
    }
}
