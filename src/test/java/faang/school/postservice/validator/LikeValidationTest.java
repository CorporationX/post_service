package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeValidationTest {
    @Mock
    private LikeRepository likeRepository;
    @InjectMocks
    private LikeValidation likeValidation;

    private final long userId = 1L;
    private final long postId = 2L;
    private final long commentId = 3L;

    private Like like;

    @BeforeEach
    void setUp() {
        Post post = Post.builder()
                .id(postId)
                .build();

        Comment comment = Comment.builder()
                .id(3L)
                .build();

        like = Like.builder()
                .userId(1L)
                .post(post)
                .comment(comment)
                .build();
    }

    @Test
    @DisplayName("Positive scenario: The user did not put two likes on the same post")
    public void testVerifyUniquenessLikePost_shouldNotThrowException() {
        Optional<Like> optionalLike = Optional.empty();
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(optionalLike);

        likeValidation.verifyUniquenessLikePost(postId, userId);

        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("Negative scenario: The user put two likes under one post")
    public void testVerifyUniquenessLikePost_shouldThrowException() {
        Optional<Like> optionalLike  = Optional.of(like);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(optionalLike);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidation.verifyUniquenessLikePost(postId, userId));

        assertEquals("The user has already liked this post", exception.getMessage());
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("Positive scenario: The user did not put two likes on the same comment")
    public void testVerifyUniquenessLikeComment_shouldNotThrowException() {
        Optional<Like> optionalLike = Optional.empty();
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(optionalLike);

        likeValidation.verifyUniquenessLikeComment(commentId, userId);

        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
    }

    @Test
    @DisplayName("Negative scenario: The user put two likes under one comment")
    public void testVerifyUniquenessLikeComment_shouldThrowException() {
        Optional<Like> optionalLike  = Optional.of(like);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(optionalLike);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidation.verifyUniquenessLikeComment(commentId, userId));

        assertEquals("The user has already liked this comment", exception.getMessage());
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
    }
}