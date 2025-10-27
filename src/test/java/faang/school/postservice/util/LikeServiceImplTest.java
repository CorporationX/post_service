package faang.school.postservice.util;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.like.LikeServiceImpl;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private LikeServiceImpl likeService;

    @Test
    void likePost_success() {
        long postId = 10L;
        long userId = 5L;

        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        Post post = new Post();
        post.setId(postId);
        when(postService.getById(postId, userId)).thenReturn(post);

        Like saved = Like.builder().id(111L).userId(userId).post(post).build();
        when(likeRepository.save(any(Like.class))).thenReturn(saved);

        LikeDto expected = new LikeDto(111L, userId, postId, null, LocalDateTime.now());
        when(likeMapper.toDto(saved)).thenReturn(expected);
        LikeDto result = likeService.likePost(postId, userId);

        assertNotNull(result);
        assertEquals(expected.id(), result.id());
        verify(userServiceClient).getUser(userId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(postService).getById(postId, userId);
        verify(likeRepository).save(any(Like.class));
        verify(likeMapper).toDto(saved);
        verifyNoMoreInteractions(userServiceClient, likeRepository, postService, likeMapper, commentService);
    }

    @Test
    void likePost_duplicateLike_throws() {
        long postId = 10L;
        long userId = 5L;

        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(new Like()));

        assertThrows(IllegalStateException.class, () -> likeService.likePost(postId, userId));

        verify(userServiceClient).getUser(userId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(postService, never()).getById(anyLong(), anyLong());
        verify(likeRepository, never()).save(any());
        verifyNoMoreInteractions(userServiceClient, likeRepository);
        verifyNoInteractions(postService, commentService, likeMapper);
    }

    @Test
    void likePost_userNotFound_translatesToIllegalArgument() {
        long postId = 10L;
        long userId = 999L;

        when(userServiceClient.getUser(userId)).thenThrow(new FeignException(404, "User not found") {});

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> likeService.likePost(postId, userId));
        assertTrue(ex.getMessage().contains("User not found"));

        verify(userServiceClient).getUser(userId);
        verifyNoInteractions(likeRepository, postService, commentService, likeMapper);
    }


    @Test
    void unlikePost_likeExists_deletes() {
        long postId = 10L;
        long userId = 5L;

        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(new Like()));

        assertDoesNotThrow(() -> likeService.unlikePost(postId, userId));

        verify(userServiceClient).getUser(userId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(likeRepository).deleteByPostIdAndUserId(postId, userId);
        verifyNoMoreInteractions(userServiceClient, likeRepository);
        verifyNoInteractions(postService, commentService, likeMapper);
    }

    @Test
    void unlikePost_noLike_noop() {
        long postId = 10L;
        long userId = 5L;

        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        likeService.unlikePost(postId, userId);

        verify(userServiceClient).getUser(userId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(likeRepository, never()).deleteByPostIdAndUserId(anyLong(), anyLong());
        verifyNoMoreInteractions(userServiceClient, likeRepository);
        verifyNoInteractions(postService, commentService, likeMapper);
    }


    @Test
    void likeComment_success() {
        long commentId = 77L;
        long userId = 5L;

        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        Comment comment = new Comment();
        comment.setId(commentId);
        when(commentService.getById(commentId, userId)).thenReturn(comment);

        Like saved = Like.builder().id(222L).userId(userId).comment(comment).build();
        when(likeRepository.save(any(Like.class))).thenReturn(saved);

        LikeDto expected = new LikeDto(222L, userId, null, commentId, LocalDateTime.now());
        when(likeMapper.toDto(saved)).thenReturn(expected);

        LikeDto result = likeService.likeComment(commentId, userId);

        assertNotNull(result);
        assertEquals(expected.id(), result.id());
        verify(userServiceClient).getUser(userId);
        verify(likeRepository).findByCommentIdAndUserId(commentId, userId);
        verify(commentService).getById(commentId, userId);
        verify(likeRepository).save(any(Like.class));
        verify(likeMapper).toDto(saved);
        verifyNoMoreInteractions(userServiceClient, likeRepository, commentService, likeMapper);
        verifyNoInteractions(postService);
    }

    @Test
    void likeComment_duplicateLike_throws() {
        long commentId = 77L;
        long userId = 5L;

        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(new Like()));

        assertThrows(IllegalStateException.class, () -> likeService.likeComment(commentId, userId));

        verify(userServiceClient).getUser(userId);
        verify(likeRepository).findByCommentIdAndUserId(commentId, userId);
        verify(commentService, never()).getById(anyLong(), anyLong());
        verify(likeRepository, never()).save(any());
        verifyNoMoreInteractions(userServiceClient, likeRepository);
        verifyNoInteractions(commentService, postService, likeMapper);
    }

    @Test
    void likeComment_userNotFound_translatesToIllegalArgument() {
        long commentId = 77L;
        long userId = 999L;

        when(userServiceClient.getUser(userId)).thenThrow(new FeignException(404, "User not found") {});

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> likeService.likeComment(commentId, userId));
        assertTrue(ex.getMessage().contains("User not found"));

        verify(userServiceClient).getUser(userId);
        verifyNoInteractions(likeRepository, commentService, postService, likeMapper);
    }

    @Test
    void unlikeComment_likeExists_deletes() {
        long commentId = 77L;
        long userId = 5L;

        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(new Like()));

        likeService.unlikeComment(commentId, userId);

        verify(userServiceClient).getUser(userId);
        verify(likeRepository).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository).deleteByCommentIdAndUserId(commentId, userId);
        verifyNoMoreInteractions(userServiceClient, likeRepository);
        verifyNoInteractions(commentService, postService, likeMapper);
    }

    @Test
    void unlikeComment_noLike_noop() {
        long commentId = 77L;
        long userId = 5L;

        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        likeService.unlikeComment(commentId, userId);

        verify(userServiceClient).getUser(userId);
        verify(likeRepository).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository, never()).deleteByCommentIdAndUserId(anyLong(), anyLong());
        verifyNoMoreInteractions(userServiceClient, likeRepository);
        verifyNoInteractions(commentService, postService, likeMapper);
    }
}
