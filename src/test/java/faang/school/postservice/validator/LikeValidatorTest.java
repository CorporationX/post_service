package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class LikeValidatorTest {

    @Mock
    UserServiceClient userServiceClient;

    @InjectMocks
    LikeValidator likeValidator;

    Long firstUserId = 1L;
    Long secondUserId = 2L;
    Like like1 = Like.builder()
            .userId(firstUserId)
            .build();
    Like like2 = Like.builder()
            .userId(secondUserId)
            .build();
    Post post1 = Post.builder()
            .likes(List.of(like1))
            .build();
    Comment comment = Comment.builder()
            .id(1L)
            .likes(List.of(like1))
            .post(post1)
            .build();
    Post post2 = Post.builder()
            .id(1L)
            .likes(List.of(like2))
            .comments(List.of(comment))
            .build();

    @Test
    void validateUserAndPostWithInvalidUserIdShouldThrowException() {
        doThrow(FeignException.FeignClientException.class).when(userServiceClient).getUser(firstUserId);
        assertThrows(FeignException.class, () -> likeValidator.validateUserAndPost(post2, firstUserId));
    }

    @Test
    void validateUserAndPostWithPostLikedYetShouldThrowException() {
        Exception exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateUserAndPost(post2, secondUserId));
        assertEquals("Post with id " + post2.getId() + " already liked by user "
                + secondUserId, exception.getMessage());
    }

    @Test
    void validateUserAndPostWithPostCommentConditionsShouldThrowException() {
        Exception exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateUserAndPost(post2, firstUserId));
        assertEquals("Post with id " + post2.getId() + " already has comment liked by user "
                + firstUserId, exception.getMessage());
    }

    @Test
    void validateUserAndCommentWithCommentLikedYetShouldThrowException() {
        comment.setLikes(List.of(like2));
        Exception exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateUserAndComment(comment, secondUserId));
        assertEquals("Comment with id " + comment.getId() + " already liked by user "
                + secondUserId, exception.getMessage());
    }

    @Test
    void validateUserAndCommentWithPostCommentConditionsShouldThrowException() {
        post1.setLikes(List.of(like2));
        Exception exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateUserAndComment(comment, secondUserId));
        assertEquals("Comment with id " + comment.getId() + " belongs to post liked by user "
                + secondUserId, exception.getMessage());
    }
}