package faang.school.postservice.service;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing and retrieving like-related information
 * for posts and comments.
 */

public interface LikeServiceInterface {
    /**
     * Retrieves a paginated list of users who have liked a specific post.
     * <p>
     * The user information is returned as {@link UserDto} objects.
     *
     * @param postId The unique identifier of the post for which to find likes. Must not be null.
     * @param pageable Pagination information (page number, size, sort order) to control the result set.
     * @return A {@link Page} of {@link UserDto} objects representing the users who liked the specified post.
     *         Returns an empty page if the post has no likes or if the postId is invalid.
     * @throws IllegalArgumentException if postId is null.
     */

    Page<UserDto> findLikersByPostId(Long postId, Pageable pageable);

    /**
     * Retrieves a paginated list of users who have liked a specific comment.
     * <p>
     * The user information is returned as {@link UserDto} objects.
     *
     * @param commentId The unique identifier of the comment for which to find likes. Must not be null.
     * @param pageable Pagination information (page number, size, sort order) to control the result set.
     * @return A {@link Page} of {@link UserDto} objects representing the users who liked the specified comment.
     *         Returns an empty page if the comment has no likes or if the commentId is invalid.
     * @throws IllegalArgumentException if commentId is null.
     */
    Page<UserDto> findLikersByCommentId(Long commentId, Pageable pageable);
}
