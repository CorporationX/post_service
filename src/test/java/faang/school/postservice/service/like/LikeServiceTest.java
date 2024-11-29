package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.exception.RecordAlreadyExistsException;
import faang.school.postservice.exception.like.LikeNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.kafka.KafkaEventProducer;
import faang.school.postservice.publisher.kafka.events.PostLikeEvent;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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
    @Mock
    private KafkaEventProducer kafkaEventProducer;

    private Post post;
    private Comment comment;
    private Like like;
    private Like likeComment;
    private Long postId;
    private Long commentId;
    private Long userId;

    @BeforeEach
    public void setUp() {
        long likeId = 1L;
        postId = 7L;
        commentId = 5L;
        userId = 2L;
        post = Post.builder().id(postId).build();
        comment = Comment.builder().id(commentId).build();
        like = Like.builder().id(likeId).userId(userId).post(post).build();
        likeComment = Like.builder().id(likeId).userId(userId).comment(comment).build();
    }

    @Test
    void testCreatePostLike_success_whenUserNotLikedPost() {
        when(postService.findPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> {
            Like likeToSave = invocation.getArgument(0);
            likeToSave.setId(1L);
            return likeToSave;
        });

        Like result = likeService.createPostLike(postId);

        verify(postService).findPostById(postId);
        verify(userContext).getUserId();
        verify(userValidator).validateUserExists(userId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(likeRepository).save(any(Like.class));

        assertEquals(1L, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(post, result.getPost());
    }

    @Test
    void testCreatePostLike_failed_whenUserAlreadyLikedPost() {
        when(postService.findPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));

        assertThrows(RecordAlreadyExistsException.class, () -> likeService.createPostLike(postId));

        verify(postService).findPostById(postId);
        verify(userContext).getUserId();
        verify(userValidator).validateUserExists(userId);
        verify(likeRepository).findByPostIdAndUserId(eq(postId), eq(userId));
        verify(likeRepository, never()).save(any(Like.class));
        verify(kafkaEventProducer, never()).sendEvent(any());
    }

    @Test
    void testDeletePostLike_success_whenUserLikedPost() {
        when(postService.findPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));

        likeService.deletePostLike(postId);

        verify(postService).findPostById(postId);
        verify(userContext).getUserId();
        verify(userValidator).validateUserExists(userId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(likeRepository).deleteByPostIdAndUserId(postId, userId);

        ArgumentCaptor<PostLikeEvent> eventCaptor = ArgumentCaptor.forClass(PostLikeEvent.class);
        verify(kafkaEventProducer).sendEvent(eventCaptor.capture());
        PostLikeEvent capturedEvent = eventCaptor.getValue();

        assertEquals(postId, capturedEvent.getPostId());
        assertEquals(LikeAction.REMOVE, capturedEvent.getLikeAction());
    }

    @Test
    void testDeletePostLike_failed_whenUserNotLikedPost() {
        when(postService.findPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        assertThrows(LikeNotFoundException.class, () -> likeService.deletePostLike(postId));

        verify(postService).findPostById(postId);
        verify(userContext).getUserId();
        verify(userValidator).validateUserExists(userId);
        verify(likeRepository).findByPostIdAndUserId(eq(postId), eq(userId));
        verify(likeRepository, never()).deleteByPostIdAndUserId(anyLong(), anyLong());
        verify(kafkaEventProducer, never()).sendEvent(any());
    }

    @Test
    void testCreateCommentLike_success_whenUserNotLikedComment() {
        when(commentService.getById(commentId)).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(likeComment);

        Like result = likeService.createCommentLike(commentId);

        verify(commentService).getById(commentId);
        verify(userContext).getUserId();
        verify(userValidator).validateUserExists(userId);
        verify(likeRepository).findByCommentIdAndUserId(eq(commentId), eq(userId));
        verify(likeRepository).save(any(Like.class));

        assertEquals(likeComment, result);
    }

    @Test
    void testCreateCommentLike_failed_whenUserAlreadyLikedComment() {
        when(commentService.getById(commentId)).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(likeComment));

        assertThrows(RecordAlreadyExistsException.class, () -> likeService.createCommentLike(commentId));

        verify(commentService).getById(commentId);
        verify(userContext).getUserId();
        verify(userValidator).validateUserExists(userId);
        verify(likeRepository).findByCommentIdAndUserId(eq(commentId), eq(userId));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testDeleteCommentLike_success_whenUserLikedComment() {
        when(commentService.getById(commentId)).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(likeComment));

        likeService.deleteCommentLike(commentId);

        verify(commentService).getById(commentId);
        verify(userContext).getUserId();
        verify(userValidator).validateUserExists(userId);
        verify(likeRepository).findByCommentIdAndUserId(eq(commentId), eq(userId));
        verify(likeRepository).deleteByCommentIdAndUserId(eq(commentId), eq(userId));
    }

    @Test
    void testDeleteCommentLike_failed_whenUserNotLikedComment() {
        when(commentService.getById(commentId)).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExists(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        assertThrows(LikeNotFoundException.class, () -> likeService.deleteCommentLike(commentId));

        verify(commentService).getById(commentId);
        verify(userContext).getUserId();
        verify(userValidator).validateUserExists(userId);
        verify(likeRepository).findByCommentIdAndUserId(eq(commentId), eq(userId));
        verify(likeRepository, never()).deleteByCommentIdAndUserId(anyLong(), anyLong());
    }
}