package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.RecordAlreadyExistsException;
import faang.school.postservice.exception.like.LikeNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.util.like.LikeTestUtil;
import faang.school.postservice.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private UserContext userContext;

    private Post post;
    private Comment comment;
    private Like like;
    private Like likeComment;
    private Long postId;
    private Long commentId;
    private Long userId;
    private Long likeId;

    @BeforeEach
    public void setUp() {
        likeId = 1L;
        postId = 7L;
        commentId = 5L;
        userId = 2L;
        post = new Post();
        comment = new Comment();
        like = LikeTestUtil.getPostLike(likeId, userId, post);
        likeComment = LikeTestUtil.getCommentLike(likeId, userId, comment);
    }

    @Test
    void testCreatePostLike_success_whenUserNotLikedPost() {
        when(postService.findPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        Like result = likeService.createPostLike(postId);

        verify(postService, times(1)).findPostById(postId);
        verify(userContext, times(1)).getUserId();
        verify(userValidator, times(1)).validateUserExists(userId);
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
        verify(likeRepository, times(1)).save(any(Like.class));

        assertEquals(like, result);
    }

    @Test
    void testCreatePostLike_failed_whenUserAlreadyLikedPost() {
        when(postService.findPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));

        assertThrows(RecordAlreadyExistsException.class, () -> likeService.createPostLike(postId));

        verify(postService, times(1)).findPostById(postId);
        verify(userContext, times(1)).getUserId();
        verify(userValidator, times(1)).validateUserExists(userId);
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testDeletePostLike_success_whenUserLikedPost() {
        when(postService.findPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));
        doNothing().when(likeRepository).deleteByPostIdAndUserId(postId, userId);

        likeService.deletePostLike(postId);

        verify(postService, times(1)).findPostById(postId);
        verify(userContext, times(1)).getUserId();
        verify(userValidator, times(1)).validateUserExists(userId);
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
        verify(likeRepository, times(1)).deleteByPostIdAndUserId(any(Long.class), any(Long.class));
    }

    @Test
    void testDeletePostLike_failed_whenUserNotLikedPost() {
        when(postService.findPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        assertThrows(LikeNotFoundException.class, () -> likeService.deletePostLike(postId));

        verify(postService, times(1)).findPostById(postId);
        verify(userContext, times(1)).getUserId();
        verify(userValidator, times(1)).validateUserExists(userId);
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
        verify(likeRepository, never()).deleteByPostIdAndUserId(any(Long.class), any(Long.class));
    }

    @Test
    void testCreateCommentLike_success_whenUserNotLikedComment() {
        when(commentService.getById(commentId)).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(likeComment);

        Like result = likeService.createCommentLike(commentId);

        verify(commentService, times(1)).getById(commentId);
        verify(userContext, times(1)).getUserId();
        verify(userValidator, times(1)).validateUserExists(userId);
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository, times(1)).save(any(Like.class));

        assertEquals(likeComment, result);
    }

    @Test
    void testCreateCommentLike_failed_whenUserAlreadyLikedComment() {
        when(commentService.getById(commentId)).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(likeComment));

        assertThrows(RecordAlreadyExistsException.class, () -> likeService.createCommentLike(commentId));

        verify(commentService, times(1)).getById(commentId);
        verify(userContext, times(1)).getUserId();
        verify(userValidator, times(1)).validateUserExists(userId);
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testDeleteCommentLike_success_whenUserLikedComment() {
        when(commentService.getById(commentId)).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(likeComment));
        doNothing().when(likeRepository).deleteByCommentIdAndUserId(commentId, userId);

        likeService.deleteCommentLike(commentId);

        verify(commentService, times(1)).getById(commentId);
        verify(userContext, times(1)).getUserId();
        verify(userValidator, times(1)).validateUserExists(userId);
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(any(Long.class), any(Long.class));
    }

    @Test
    void testDeleteCommentLike_failed_whenUserNotLikedComment() {
        when(commentService.getById(commentId)).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        assertThrows(LikeNotFoundException.class, () -> likeService.deleteCommentLike(commentId));

        verify(commentService, times(1)).getById(commentId);
        verify(userContext, times(1)).getUserId();
        verify(userValidator, times(1)).validateUserExists(userId);
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository, never()).deleteByCommentIdAndUserId(any(Long.class), any(Long.class));
    }
}
