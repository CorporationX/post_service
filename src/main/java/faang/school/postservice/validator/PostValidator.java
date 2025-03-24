package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataUpdateException;
import faang.school.postservice.exception.RequiredOwnerException;
import faang.school.postservice.exception.SinglePostAuthorException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validatedOwnerPost(PostDto postDTO) {
        if (postDTO.authorId() == null && postDTO.projectId() == null) {
            throw new RequiredOwnerException("Author post must be project or user");
        }

        if (postDTO.authorId() != null && postDTO.projectId() != null) {
            throw new SinglePostAuthorException("Post can have only one author: project or user");
        }

        if (postDTO.authorId() != null) {
            getUserById(postDTO.authorId());
        }

        if (postDTO.projectId() != null) {
            getProjectById(postDTO.projectId());
        }
    }

    @Retryable(retryFor = {FeignException.class},
            noRetryFor = {FeignException.NotFound.class},
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public UserDto getUserById(Long userId) {
        try {
            return userServiceClient.getUser(userId);
        } catch (FeignException e) {
            log.error("User with ID {} not found", userId, e);
            throw new EntityNotFoundException("User with ID " + userId + " not found");
        }
    }

    @Retryable(retryFor = {FeignException.class},
            noRetryFor = {FeignException.NotFound.class},
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public ProjectDto getProjectById(Long projectId) {
        try {
            return projectServiceClient.getProjectById(projectId);
        } catch (FeignException e) {
            log.error("Project with ID {} not found", projectId, e);
            throw new EntityNotFoundException("Project with ID " + projectId + " not found");
        }
    }

    public void validateAuthorForUpdate(Post post, PostDto updatePost) {
        if (post.getAuthorId() != null && !Objects.equals(post.getAuthorId(), updatePost.authorId())) {
            throw new DataUpdateException("Can not deleted or changed the author of the post");
        }

        if (post.getProjectId() != null && !Objects.equals(post.getProjectId(), updatePost.projectId())) {
            throw new DataUpdateException("Can not deleted or change the author of the post");
        }
    }
}
