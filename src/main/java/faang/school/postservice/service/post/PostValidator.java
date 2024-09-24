package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.post.ImmutablePostDataException;
import faang.school.postservice.exception.post.PostAlreadyDeletedException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostWOAuthorException;
import faang.school.postservice.exception.post.PostWithTwoAuthorsException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validatePublished(Post post) {
        if (post.isPublished()) {
            throw new PostAlreadyPublishedException();
        }
    }

    public void validateBeforeUpdate(PostDto postDto, Post post) {
        checkChangedAuthor(postDto, post);
    }

    public void validateBeforeCreate(PostDto postDto) {
        checkAuthor(postDto);
        checkAuthorOrProject(postDto);
    }

    public void validateDeleted(Post entity) {
        if (entity.isDeleted()) {
            throw new PostAlreadyDeletedException();
        }
    }

    private void checkChangedAuthor(PostDto postDto, Post post) {
        if (!Objects.equals(postDto.getAuthorId(), post.getAuthorId())
                || !Objects.equals(postDto.getProjectId(), post.getProjectId())) {
            throw new ImmutablePostDataException("Автора нельзя изменить.");
        }
    }

    private void checkAuthor(PostDto postDto) {
        boolean flagFirst = postDto.getAuthorId() == null;
        boolean flagSecond = postDto.getProjectId() == null;
        if (flagFirst && flagSecond) {
            throw new PostWOAuthorException();
        }
        if (flagFirst == flagSecond) {
            throw new PostWithTwoAuthorsException();
        }
    }

    private void checkAuthorOrProject(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if (authorId != null) {
            userServiceClient.getUser(authorId);
            return;
        }
        if (projectId != null) {
            projectServiceClient.getProject(projectId);
        }
    }
}
