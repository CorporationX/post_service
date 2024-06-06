package faang.school.postservice.service.post;


import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static faang.school.postservice.exception.message.PostOperationExceptionMessage.DELETED_STATUS_UPDATE_EXCEPTION;
import static faang.school.postservice.exception.message.PostOperationExceptionMessage.LIKES_UPDATE_EXCEPTION;
import static faang.school.postservice.exception.message.PostOperationExceptionMessage.PUBLISHED_DATE_UPDATE_EXCEPTION;
import static faang.school.postservice.exception.message.PostValidationExceptionMessage.NON_EXISTING_PROJECT_EXCEPTION;
import static faang.school.postservice.exception.message.PostValidationExceptionMessage.NON_EXISTING_USER_EXCEPTION;
import static faang.school.postservice.exception.message.PostValidationExceptionMessage.NON_MATCHING_AUTHORS_EXCEPTION;

@Component
@RequiredArgsConstructor
class PostVerifier {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void verifyAuthorExistence(Long authorId, Long projectId) {
        if (authorId != null) {
            verifyUserExistence(authorId);
        }

        if (projectId != null) {
            verifyProjectExistence(projectId);
        }
    }

    public void verifyUserExistence(long userId) {
        if (!userServiceClient.existsById(userId)) {
            throw new DataValidationException(NON_EXISTING_USER_EXCEPTION.getMessage());
        }
    }

    public void verifyProjectExistence(long projectId) {
        if (!projectServiceClient.existsById(projectId)) {
            throw new DataValidationException(NON_EXISTING_PROJECT_EXCEPTION.getMessage());
        }
    }

    public void verifyPostMatchingSystem(PostDto postDto, Post postToBeUpdated) {
        verifyAuthorMatchingSystem(postDto.getAuthorId(), postToBeUpdated.getAuthorId());
        verifyAuthorMatchingSystem(postDto.getProjectId(), postToBeUpdated.getProjectId());

        verifyLikesAndCommentsMatchingSystem(postDto, postToBeUpdated);

        verifyPublicationDateMatchingSystem(postDto, postToBeUpdated);

        verifyDeletedStatusMatchingSystem(postDto, postToBeUpdated);
    }

    private void verifyLikesAndCommentsMatchingSystem(PostDto postDto, Post postToBeUpdated) {
        Stream<Long> commentsIds = postToBeUpdated.getComments().stream()
                .map(Comment::getId);
        Stream<Long> likesIds = postToBeUpdated.getLikes().stream()
                .map(Like::getId);
        verifyReviewsMathcingSystem(commentsIds, postDto.getCommentsIds());
        verifyReviewsMathcingSystem(likesIds, postDto.getLikesIds());
    }

    private void verifyDeletedStatusMatchingSystem(PostDto postDto, Post postToBeUpdated) {
        if (!postDto.isDeleted() == postToBeUpdated.isDeleted()) {
            throw new DataValidationException(DELETED_STATUS_UPDATE_EXCEPTION.getMessage());
        }
    }

    private void verifyPublicationDateMatchingSystem(PostDto postDto, Post postToBeUpdated) {
        LocalDateTime publishedAtFromDto = postDto.getPublishedAt();
        if (publishedAtFromDto != null && postToBeUpdated.isPublished() && !postToBeUpdated.getPublishedAt().equals(publishedAtFromDto)) {
            throw new DataValidationException(PUBLISHED_DATE_UPDATE_EXCEPTION.getMessage());
        }
    }

    private void verifyReviewsMathcingSystem(Stream<Long> postToBeUpdated, List<Long> postDto) {
        Set<Long> likesOfPostToBeUpdated = postToBeUpdated
                .collect(Collectors.toSet());
        if (!likesOfPostToBeUpdated.containsAll(postDto)) {
            throw new DataValidationException(LIKES_UPDATE_EXCEPTION.getMessage());
        }
    }

    private void verifyAuthorMatchingSystem(Long postDto, Long postToBeUpdated) {
        if (postDto != null && !Objects.equals(postToBeUpdated, postDto)) {
            throw new DataValidationException(NON_MATCHING_AUTHORS_EXCEPTION.getMessage());
        }
    }
}
