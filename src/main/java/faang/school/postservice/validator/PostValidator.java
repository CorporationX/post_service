package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepo;
    private final UserContext userContext;

    public void validatePost(PostDto postDto) {
        validatePostExists(postDto.getId());
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("Post cannot belong to both author and project");
        }
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("Post must belong to either author or project");
        }
    }

    public void validatePostByOwner(long postId, long ownerId) {
        Post post = postRepo.findById(postId).get();

        if (post.getAuthorId() != null) {
            if (post.getAuthorId() != ownerId) {
                throw new DataValidationException("You are not the author of the post");
            }
        } else {
            if (post.getProjectId() != ownerId) {
                throw new DataValidationException("Project is not the author of the post");
            }
        }
    }

    public void validatePostOwnerExists(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            userServiceClient.existsUserById(postDto.getAuthorId());
        } else {
            projectServiceClient.existsProjectById(postDto.getProjectId());
        }

    }

    public void validateAuthor(long authorId) {
        userServiceClient.existsUserById(authorId);
    }

    public void validateProject(long projectId) {
        projectServiceClient.existsProjectById(projectId);
    }

    public void validatePostExists(long id) {
        if (postRepo.existsById(id)) {
            throw new DataValidationException("Post with id: " + id + " already exists");
        }
    }

    public void validateAccessAndContent(PostDto postDto) {
        validateAccessToPost(postDto.getAuthorId(), postDto.getProjectId());
        validatePostContent(postDto.getContent());
    }

    public void validatePostContent(String content) {
        if (content == null || content.isBlank()) {
            throw new DataValidationException("Post content cannot be blank");
        }
    }

    public void validateAccessToPost(Long postAuthorId, Long postProjectId) {
        if (Objects.equals(postAuthorId, postProjectId)) {
            throw new DataValidationException("Post cannot belong to both author and project or be null");
        }

        long userId = userContext.getUserId();

        if (postAuthorId != null) {
            validateOwnershipUserToPost(postAuthorId, userId);
        } else {
            validateOwnershipProjectToPost(postProjectId, userId);
        }
    }

    private void validateOwnershipProjectToPost(Long postProjectId, long userId) {
        if (!projectServiceClient.existProjectById(postProjectId)) {
            throw new EntityNotFoundException(String.format("Project with id %s not found", postProjectId));
        }
        List<Long> projectIdsUserHasAccess = projectServiceClient.getAll()
                .stream().filter(prj -> prj.getOwnerId() == userId)
                .map(ProjectDto::getId)
                .toList();
        if (!projectIdsUserHasAccess.contains(postProjectId)) {
            throw new SecurityException("Project is not author of the post");
        }
    }

    private void validateOwnershipUserToPost(Long postAuthorId, long userId) {
        if (!userServiceClient.existsUserById(postAuthorId)) {
            throw new EntityNotFoundException(String.format("User with id %s not found", postAuthorId));
        }
        if (postAuthorId != userId) {
            throw new SecurityException("You are not the author of the post");
        }
    }
}
