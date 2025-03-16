package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDTO;
import faang.school.postservice.exception.DataUpdateException;
import faang.school.postservice.exception.RequiredOwnerException;
import faang.school.postservice.exception.SinglePostAuthorException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
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
    private final PostRepositoryAdapter postRepositoryAdapter;

    public void validatedOwnerPost(PostDTO postDTO) {
        if (postDTO.authorId() == null && postDTO.projectId() == null) {
            throw new RequiredOwnerException("Author post must be project or user");
        }

        if (postDTO.authorId() != null && postDTO.projectId() != null) {
            throw new SinglePostAuthorException("Post can have only one author: project or user");
        }

        if (postDTO.authorId() != null) {
            userOwnerOfThePost(postDTO.authorId());
        }

        if (postDTO.projectId() != null) {
            projectOwnerOfThePost(postDTO.projectId());
        }
    }

    @Retryable(retryFor = {FeignException.class},
            noRetryFor = {FeignException.NotFound.class},
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void userOwnerOfThePost(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            log.error("User with id {} not found", userId, e);
            throw new EntityNotFoundException("User not found");
        }
    }

    @Retryable(retryFor = {FeignException.class},
            noRetryFor = {FeignException.NotFound.class},
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void projectOwnerOfThePost(Long projectId) {
        try {
            projectServiceClient.getProjectById(projectId);
        } catch (FeignException e) {
            log.error("Project with id {} not found", projectId, e);
            throw new EntityNotFoundException("Project not found");
        }
    }

    public Post findPostWithId(long postId) {
        return postRepositoryAdapter.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("This post not found"));
    }

    public void validateAuthorForUpdate(Post post, PostDTO updatePost) {
        if (post.getAuthorId() != null && !Objects.equals(post.getAuthorId(), updatePost.authorId())) {
            throw new DataUpdateException("Can not deleted or changed the author of the post");
        }

        if (post.getProjectId() != null && !Objects.equals(post.getProjectId(), updatePost.projectId())) {
            throw new DataUpdateException("Can not deleted or change the author of the post");
        }
    }
}
