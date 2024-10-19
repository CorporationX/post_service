package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.post.ImmutablePostDataException;
import faang.school.postservice.exception.post.PostAlreadyDeletedException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostWithoutAuthorException;
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

    public void validateBeforePublishing(Post post) {
        if (post.isPublished()) {
            throw new PostAlreadyPublishedException(String.format("Post by id %s already published", post.getId()));
        }
        if (post.isDeleted()) {
            throw new PostAlreadyDeletedException(String.format("Post by id %s was deleted", post.getId()));
        }
    }

    public void validateBeforeUpdate(PostDto postDto, Post post) {
        checkChangedAuthor(postDto, post);
    }

    public void validateBeforeCreate(PostDto postDto) {
        checkOwner(postDto);
        checkExistingOwner(postDto);
    }

    public void validateBeforeDeleting(Post post) {
        if (post.isDeleted()) {
            throw new PostAlreadyDeletedException(String.format("Post by id %s already deleted", post.getId()));
        }
    }

    private void checkChangedAuthor(PostDto dto, Post entity) {
        if (!Objects.equals(dto.getAuthorId(), entity.getAuthorId())
                || !Objects.equals(dto.getProjectId(), entity.getProjectId())) {
            throw new ImmutablePostDataException("The author cannot be changed");
        }
    }

    private void checkOwner(PostDto postDto) {
        boolean isAuthor = postDto.getAuthorId() == null;
        boolean isProject = postDto.getProjectId() == null;
        if (isAuthor && isProject) {
            throw new PostWithoutAuthorException();
        }
        if (isAuthor == isProject) {
            throw new PostWithTwoAuthorsException();
        }
    }

    private void checkExistingOwner(PostDto postDto) {
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
