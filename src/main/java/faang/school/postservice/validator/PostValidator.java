package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.post.MixedAuthorshipException;
import faang.school.postservice.exception.post.NoAuthorshipException;
import faang.school.postservice.exception.post.ProjectNotExistentException;
import faang.school.postservice.exception.post.RepeatPublishException;
import faang.school.postservice.exception.post.UserNotExistentException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validateCreate(CreatePostDto createPostDto) {
        checkNoAuthorship(createPostDto);
        checkMixedAuthorship(createPostDto);

        if (createPostDto.authorId() != null) {
            checkUserExists(createPostDto.authorId());
        }
        if (createPostDto.projectId() != null) {
            checkProjectExists(createPostDto.projectId());
        }
    }

    public void checkProjectExists(@NonNull Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
        } catch (FeignException feignException) {
            handleFeignException(
                    feignException,
                    new ProjectNotExistentException(String.format("No project for provided project id: %d found.", projectId)),
                    "project",
                    projectId
            );
        }
    }

    public void checkUserExists(@NonNull Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException feignException) {

            handleFeignException(
                    feignException,
                    new UserNotExistentException(String.format("No user for provided user id: %d found.", authorId)),
                    "user",
                    authorId
            );
        }
    }

    private void handleFeignException(FeignException e, RuntimeException notFoundException, String entityName, Long entityId) {
        if (e instanceof FeignException.NotFound) {
            throw notFoundException;
        }
        throw new RuntimeException(
                String.format("Unknown problem getting %s id: %d from external service. Problem: %s",
                        entityName,
                        entityId,
                        e.getMessage())
        );
    }

    private void checkMixedAuthorship(CreatePostDto createPostDto) {
        if (createPostDto.authorId() != null && createPostDto.projectId() != null) {
            throw new MixedAuthorshipException();
        }
    }

    private void checkNoAuthorship(CreatePostDto createPostDto) {
        if (createPostDto.authorId() == null && createPostDto.projectId() == null) {
            throw new NoAuthorshipException();
        }
    }

    public void validatePublish(@NonNull Post post) {
        if (post.isPublished()) {
            throw new RepeatPublishException("Cannot publish, post already published.");
        }
        validateNotDeleted(post);
    }

    public void validateUpdate(@NonNull Post post) {
        validateNotDeleted(post);
    }

    private void validateNotDeleted(Post post) {
        if (post.isDeleted()) {
            throw new EntityNotFoundException(String.format("No active posts with postId: %d found", post.getId()));
        }
    }

}
