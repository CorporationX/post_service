package faang.school.postservice.service;

import faang.school.postservice.dto.likes.BaseFilterDto;
import faang.school.postservice.dto.likes.LikeDto;
import faang.school.postservice.dto.user.UserDto;

import java.util.List;

/**
 * Service for managing likes on posts and comments by a user.
 */
public interface LikeService {

    /**
     * Like a post.
     *
     * @param userId ID of the user performing the like.
     * @param postId ID of the post to be liked.
     * @return LikeDto representing the information of the created like.
     */
    LikeDto likePost(long userId, long postId);

    /**
     * Remove a like from a post.
     *
     * @param userId ID of the user performing the unlike.
     * @param postId ID of the post from which the like is to be removed.
     */
    void deletePostLike(long userId, long postId);

    /**
     * Like a comment.
     *
     * @param userId    ID of the user performing the like.
     * @param commentId ID of the comment to be liked.
     * @return LikeDto representing the information of the created like.
     */
    LikeDto likeComment(long userId, long commentId);

    /**
     * Remove a like from a comment.
     *
     * @param userId    ID of the user performing the unlike.
     * @param commentId ID of the comment from which the like is to be removed.
     */
    void deleteCommentLike(long userId, long commentId);

    /**
     * Retrieves a list of users associated with a specific post ID.
     *
     * @param postId the ID of the post to retrieve users for
     * @param filter the filter to retrieve users for
     * @return a ResponseEntity containing a list of UserDto objects associated with the post ID
     */
    List<UserDto> usersByPostId(long postId, BaseFilterDto filter);

    /**
     * Retrieves a list of users associated with a specific comment ID.
     *
     * @param commentId the ID of the comment to retrieve users for
     * @param filter    the filter to retrieve users for
     * @return a ResponseEntity containing a list of UserDto objects associated with the comment ID
     */
    List<UserDto> usersByCommentId(long commentId, BaseFilterDto filter);
}
