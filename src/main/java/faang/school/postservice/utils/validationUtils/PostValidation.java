package faang.school.postservice.utils.validationUtils;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.exception.InvalidPostAuthorsException;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostValidation {
    public static final String POST_AUTHORS_ERROR = "Post must be created either by author or by project";
    public static final String USER_ID_CANT_BE_NULL = "User ID can't be null";
    public static final String PROJECT_ID_CANT_BE_NULL = "projectId ID can't be null";
    public static final String POST_DRAFT_CANT_BE_DELETED = "Post draft can't be deleted";
    public static final String POST_DRAFT_CANT_BE_PUBLISHED = "Post draft can't be published";
    public static final String POST_ALREADY_PUBLISHED = "Post with ID %d has already been published";
    public static final String POST_DELETED = "Post with ID %d has been deleted";
    public static final String POST_ID_CANT_BE_NULL = "Post ID can't be null";
    public static final String CONTENT_CANT_BE_NULL = "Content of post can't be null";
    public static final String CANT_DELETE_POST_DURING_UPDATE = "Can't delete post during update";

    public static void validatePostAuthors(PostRequestDto postRequestDto) {
        if ((postRequestDto.getAuthorId() != null && postRequestDto.getProjectId() != null)
                || (postRequestDto.getAuthorId() == null && postRequestDto.getProjectId() == null)) {
            log.error(POST_AUTHORS_ERROR);
            throw new InvalidPostAuthorsException(POST_AUTHORS_ERROR);
        }
    }

    public static void validateUserId(Long userId) {
        if (userId == null) {
            log.error(USER_ID_CANT_BE_NULL);
            throw new IllegalArgumentException(USER_ID_CANT_BE_NULL);
        }
    }

    public static void validateProjectId(Long projectId) {
        if (projectId == null) {
            log.error(PROJECT_ID_CANT_BE_NULL);
            throw new IllegalArgumentException(PROJECT_ID_CANT_BE_NULL);
        }
    }

    public static void validatePostDraftCreation(PostRequestDto postRequestDto) {
        if (postRequestDto.isDeleted()) {
            log.error(POST_DRAFT_CANT_BE_DELETED);
            throw new IllegalArgumentException(POST_DRAFT_CANT_BE_DELETED);
        }
        if (postRequestDto.isPublished()) {
            log.error(POST_DRAFT_CANT_BE_PUBLISHED);
            throw new IllegalArgumentException(POST_DRAFT_CANT_BE_PUBLISHED);
        }
        if (postRequestDto.getContent() == null) {
            log.error(CONTENT_CANT_BE_NULL);
            throw new IllegalArgumentException(CONTENT_CANT_BE_NULL);
        }
    }

    public static void validatePostUpdate(PostRequestDto postRequestDto) {
        if (postRequestDto.isDeleted()) {
            log.error(CANT_DELETE_POST_DURING_UPDATE);
            throw new IllegalArgumentException(CANT_DELETE_POST_DURING_UPDATE);
        }
        if (postRequestDto.getContent() == null) {
            log.error(CONTENT_CANT_BE_NULL);
            throw new IllegalArgumentException(CONTENT_CANT_BE_NULL);
        }
    }

    public static void validatePostInPublishing(Post post) {
        if (post.isPublished()) {
            String message = String.format(POST_ALREADY_PUBLISHED, post.getId());
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        if (post.isDeleted()) {
            String message = String.format(POST_DELETED, post.getId());
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    public static void validatePostId(Long postId) {
        if (postId == null) {
            log.error(POST_ID_CANT_BE_NULL);
            throw new IllegalArgumentException(POST_ID_CANT_BE_NULL);
        }
    }
}
