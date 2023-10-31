package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.SamePostAuthorException;
import faang.school.postservice.exception.UpdatePostException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userService;
    private final ProjectServiceClient projectService;

    public void validateData(PostDto postDto) {
        Long userId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if (userId != null && projectId != null) {
            throw new SamePostAuthorException("The author of the post cannot be both a user and a project");
        }
        if (userId != null) {
            validateUserId(userId);
        } else {
            validateProjectId(projectId);
        }
    }

    public void validateAuthorUpdate(Post post, PostDto updatePost) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();
        Long updateAuthorId = updatePost.getAuthorId();
        Long updateProjectId = updatePost.getProjectId();

        if (authorId != null) {
            if (updateAuthorId == null || !updateAuthorId.equals(authorId)) {
                throw new UpdatePostException("Author of the post cannot be deleted or changed");
            }
        } else {
            if (updateProjectId == null || !updateProjectId.equals(projectId)) {
                throw new UpdatePostException("Author of the post cannot be deleted or changed");
            }
        }
    }

    public void validateUserId(long id) {
        try {
            userService.getUser(id);
        } catch (FeignException e) {
            throw new EntityNotFoundException("This user is not found");
        }
    }

    public void validateProjectId(long id) {
        try {
            projectService.getProject(id);
        } catch (FeignException e) {
            throw new EntityNotFoundException("This project is not found");
        }
    }
}
