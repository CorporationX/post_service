package faang.school.postservice.validation;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like.LikeValidatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {
    private final static long DEFAULT_ID = 1L;
    private final static long DEFAULT_USER_ID = 1L;
    private final static long DEFAULT_PROJECT_ID = 4L;
    private final static boolean DEFAULT_TRUE_SIGN = true;
    private final static boolean DEFAULT_FALSE_SIGN = false;


    @InjectMocks
    private LikeValidatorImpl likeValidator;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;

    private final long postId = DEFAULT_ID;
    private final long commentId = DEFAULT_ID;
    private final long likeId = DEFAULT_ID;
    private final long userId = DEFAULT_USER_ID;
    private final long authorId = DEFAULT_USER_ID;
    private final long projectId = DEFAULT_PROJECT_ID;
    private final boolean isPublished = DEFAULT_TRUE_SIGN;
    private final boolean isDeleted = DEFAULT_FALSE_SIGN;
    private final boolean isTrue = DEFAULT_TRUE_SIGN;
    private final boolean isFalse = DEFAULT_FALSE_SIGN;

    Post post = Post.builder()
            .id(postId)
            .likes(new ArrayList<>())
            .authorId(authorId)
            .content("Post #1")
            .comments(new ArrayList<>())
            .projectId(projectId)
            .published(isPublished)
            .deleted(isDeleted)
            .build();

    Comment comment = Comment.builder()
            .id(commentId)
            .authorId(authorId)
            .content("Comment #1")
            .likes(new ArrayList<>())
            .post(post)
            .build();

    Like like = Like.builder()
            .id(likeId)
            .userId(userId)
            .post(post)
            .comment(comment)
            .build();

    @Test
    void testSuccessfullyLikeOnPostSet() {
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        Post resultedPost = likeValidator.validateLikeOnPost(postId, userId, isTrue);
        assertEquals(post, resultedPost);
        verify(postRepository, times(1)).findById(eq(postId));
        verify(likeRepository, times(1)).findByPostIdAndUserId(eq(postId), eq(userId));
    }

    @Test
    void testSuccessfullyLikeOnCommentSet() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        when(likeRepository.findByCommentIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        Comment resultedComment = likeValidator.validateLikeOnComment(commentId, userId, isTrue);
        assertEquals(comment, resultedComment);
        verify(commentRepository, times(1)).findById(eq(commentId));
        verify(likeRepository, times(1)).findByCommentIdAndUserId(eq(commentId), eq(userId));
    }

    @Test
    void testLikeOnPostSetWhenPostDoesNotExist() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()-> likeValidator.validateLikeOnPost(postId, userId, isTrue));
    }

    @Test
    void testLikeOnCommentSetWhenCommentDoesNotExist() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> likeValidator.validateLikeOnComment(commentId, userId, isTrue));
    }

    @Test
    void testLikeOnPostSetWhenLikeAlreadyExists() {
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.ofNullable(like));
        assertThrows(DataValidationException.class,
                () -> likeValidator.validateLikeOnPost(postId, userId, isTrue));
    }

    @Test
    void testLikeOnCommentSetWhenLikeAlreadyExists() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.ofNullable(like));
        assertThrows(DataValidationException.class,
                () -> likeValidator.validateLikeOnComment(commentId, userId, isTrue));
    }

    @Test
    void testSuccessfullyLikeOnPostUnset() {
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.ofNullable(like));
        Post resultedPost = likeValidator.validateLikeOnPost(postId, userId, isFalse);
        assertEquals(post, resultedPost);
        verify(postRepository, times(1)).findById(eq(postId));
        verify(likeRepository, times(1)).findByPostIdAndUserId(eq(postId), eq(userId));
    }

    @Test
    void testSuccessfullyLikeOnCommentUnset() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        when(likeRepository.findByCommentIdAndUserId(postId, userId)).thenReturn(Optional.ofNullable(like));
        Comment resultedComment = likeValidator.validateLikeOnComment(commentId, userId, isFalse);
        assertEquals(comment, resultedComment);
        verify(commentRepository, times(1)).findById(eq(commentId));
        verify(likeRepository, times(1)).findByCommentIdAndUserId(eq(commentId), eq(userId));
    }

    @Test
    void testLikeOnPostUnsetWhenPostDoesNotExist() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()-> likeValidator.validateLikeOnPost(postId, userId, isFalse));
    }

    @Test
    void testLikeOnCommentUnsetWhenCommentDoesNotExist() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> likeValidator.validateLikeOnComment(commentId, userId, isFalse));
    }

    @Test
    void testLikeOnPostUnsetWhenLikeDoesNotExist() {
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> likeValidator.validateLikeOnPost(postId, userId, isFalse));
    }

    @Test
    void testLikeOnCommentUnsetWhenLikeDoesNotExist() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> likeValidator.validateLikeOnComment(commentId, userId, isFalse));
    }
}
