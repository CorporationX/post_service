package faang.school.postservice.service.post.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.DraftPostDto;
import faang.school.postservice.dto.post.UpdatablePostDto;
import faang.school.postservice.dto.resource.UpdatableResourceDto;
import faang.school.postservice.exception.exceptionmessages.PostServiceExceptionMessage;
import faang.school.postservice.exception.post.PostDeletedException;
import faang.school.postservice.exception.post.PostAlreadyPublished;
import faang.school.postservice.exception.post.UnexistentPostPublisher;
import faang.school.postservice.exception.validation.DataValidationException;
import faang.school.postservice.exception.exceptionmessages.ValidationExceptionMessage;
import faang.school.postservice.model.Post;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostServiceValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final long obsolescencePeriodDatePublication;

    public void validatePostPublisher(Long authorId, Long projectId) {

        validatePublisherId(authorId, projectId);

        if (authorId != null && projectId == null) {
            validateAuthor(authorId);
        }

        if (projectId != null && authorId == null) {
            validateProject(projectId);
        }
    }

    public void validatePublisherId(Long authorId, Long projectId) {
        if (authorId == null && projectId == null) {
            throw new DataValidationException(ValidationExceptionMessage.POST_WITHOUT_PUBLISHER);
        }

        if (authorId != null && projectId != null) {
            throw new DataValidationException(ValidationExceptionMessage.PUBLISHER_COLLISION, projectId, authorId);
        }
    }

    public void validateAuthor(Long authorId) {
        if (authorId == null) {
            throw new UnexistentPostPublisher(PostServiceExceptionMessage.USER_DOESNT_EXIST, authorId);
        }

        boolean authorAreNotExist = userServiceClient.getUser(authorId) == null;

        if (authorAreNotExist) {
            throw new UnexistentPostPublisher(PostServiceExceptionMessage.USER_DOESNT_EXIST, authorId);
        }
    }

    public void validateProject(Long projectId) {
        if (projectId == null) {
            throw new UnexistentPostPublisher(PostServiceExceptionMessage.PROJECT_DOESNT_EXIST, projectId);
        }

        boolean projectAreNotExist = projectServiceClient.getProject(projectId) == null;

        if (projectAreNotExist) {
            throw new UnexistentPostPublisher(PostServiceExceptionMessage.PROJECT_DOESNT_EXIST, projectId);
        }
    }

    public void validateScheduledPublicationDate(@NotNull LocalDateTime publicationDate) {
        LocalDateTime now = LocalDateTime.now();
        long minutesDifference = Duration.between(publicationDate, now).toMinutes();


        if (minutesDifference > obsolescencePeriodDatePublication) {
            throw new DataValidationException(ValidationExceptionMessage.OUT_TO_DATE_SCHEDULED_TIME);
        }
    }

    public void validateCreatablePostDraft(DraftPostDto draftPostDto) {
        validatePostPublisher(draftPostDto.getAuthorId(), draftPostDto.getProjectId());
        if (draftPostDto.getScheduledAt() != null){
            validateScheduledPublicationDate(draftPostDto.getScheduledAt());
        }
    }

    public void validateUpdatablePost(UpdatablePostDto updatablePostDto) {

        if (updatablePostDto.getContent() != null) {
            validateContent(updatablePostDto.getContent());
        }

        boolean isInvalidStateScheduledAt = updatablePostDto.getScheduledAt() != null &&
                updatablePostDto.isDeleteScheduledAt();

        if (isInvalidStateScheduledAt) {
            throw new DataValidationException(
                    ValidationExceptionMessage.COLLISION_STATE_OF_UPDATABLE_SCHEDULED_TIME,
                    updatablePostDto.getPostId()
            );
        }

        if (updatablePostDto.getScheduledAt() != null) {
            validateScheduledPublicationDate(updatablePostDto.getScheduledAt());
        }

        List<UpdatableResourceDto> resources = updatablePostDto.getResource();

        if (resources != null && !resources.isEmpty()) {
            validateUpdatableResources(resources);
        }
    }

    public void validateContent(@NotNull String postContent) {
        if (postContent.isBlank() || postContent.isEmpty()) {
            throw new DataValidationException(ValidationExceptionMessage.INVALID_POST_CONTENT);
        }
    }

    public void validateUpdatableResources(@NotNull List<UpdatableResourceDto> updatableResources) {
        updatableResources.forEach(this::validateUpdatableResource);
    }

    public void validateUpdatableResource(UpdatableResourceDto updatableResource) {
        if (updatableResource == null) {
            throw new DataValidationException(ValidationExceptionMessage.UPDATABLE_RESOURCE_IS_NULL);
        }

        Long idUpdatableRes = updatableResource.getResourceId();
        MultipartFile mediaUpdatableRes = updatableResource.getResource();

        boolean isStateUndefinedUpdatableRes = idUpdatableRes == null && mediaUpdatableRes == null;

        if (isStateUndefinedUpdatableRes) {
            throw new DataValidationException(ValidationExceptionMessage.STATE_OF_UPDATABLE_RESOURCE_IS_NOT_SET);
        }
    }

    public void validatePublishablePost(Post post) {

        verifyPostDeletion(post);

        if (post.isPublished()) {
            throw new PostAlreadyPublished(
                    post.getId()
            );
        }
    }

    public void verifyPostDeletion(Post post){
        if (post.isDeleted()) {
            throw new PostDeletedException(
                    PostServiceExceptionMessage.REQUESTED_POST_DELETED,
                    post.getId()
            );
        }
    }

    public void validateDeletablePost(Post post) {
        if (post.isDeleted()) {
            throw new PostDeletedException(
                    PostServiceExceptionMessage.POST_ALREADY_DELETED,
                    post.getId()
            );
        }
    }
}
