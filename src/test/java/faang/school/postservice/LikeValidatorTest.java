package faang.school.postservice;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {
    @InjectMocks
    LikeValidator likeValidator;
    private Like like1;
    private Like like2;
    private List<Like> likes;
    private List<Like> likes2;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        post = new Post();
        comment = new Comment();
        like1 = new Like();
        like2 = new Like();
        likes = List.of(like1);
        likes2 = List.of(like2);
    }


    @Test
    void validatePostAlreadyLiked() {
        like1.setUserId(1L);
        post.setLikes(likes);

        assertThrows(DataValidationException.class, () -> likeValidator.validateLike(like1, post));
    }

    @Test
    void validateCommentAlreadyLiked() {
        like1.setUserId(1L);
        like2.setUserId(2L);
        post.setLikes(likes2);
        comment.setLikes(likes);
        post.setComments(List.of(comment));

        assertThrows(DataValidationException.class, () -> likeValidator.validateLike(like1, post));
    }

    @Test
    void validateLikeSuccessful() {
        like1.setUserId(1L);
        like2.setUserId(2L);
        post.setLikes(likes2);
        comment.setLikes(likes2);
        post.setComments(List.of(comment));

        assertDoesNotThrow(() -> likeValidator.validateLike(like1, post));
    }

    @Test
    void validatePostAndCommentPostLiked() {
        like1.setUserId(1L);
        like2.setUserId(2L);
        post.setLikes(likes2);
        comment.setLikes(likes2);
        post.setComments(List.of(comment));

        assertThrows(DataValidationException.class, () -> likeValidator.validateLike(like2, post));
    }

    @Test
    void validatePostAndCommentCommentLiked() {
        like1.setUserId(1L);
        like2.setUserId(2L);
        post.setLikes(likes);
        comment.setLikes(likes2);
        post.setComments(List.of(comment));

        assertThrows(DataValidationException.class, () -> likeValidator.validateLike(like2, post));
    }

    @Test
    void validatePostAndCommentSuccessful() {
        like1.setUserId(1L);
        like2.setUserId(2L);
        post.setLikes(likes2);
        comment.setLikes(likes2);
        post.setComments(List.of(comment));

        assertDoesNotThrow(() -> likeValidator.validateLike(like1, post));
    }

}
