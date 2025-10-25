package faang.school.postservice.util.service.likes;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.AlreadyLikedException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.likes.LikeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private LikeServiceImpl likeService;

    @Test
    void createPostLike_whenPostNotFound_shouldThrowException() {
        when(userContext.getUserId()).thenReturn(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> likeService.createPostLike(1L)
        );

        verify(likeRepository, never()).save(any());
    }

    @Test
    void createPostLike_whenFindLike_shouldThrowException() {
        Post post = new Post();
        post.setId(1L);

        Like like = new Like();

        when(userContext.getUserId()).thenReturn(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.of(like));

        assertThrows(
                AlreadyLikedException.class,
                () -> likeService.createPostLike(1L)
        );

        verify(likeRepository, never()).save(any());
    }

    @Test
    void createPostLike_shouldSaveLike_whenLikeDoesNotExistAndPostExist() {
        Post post = new Post();
        post.setId(1L);

        when(userContext.getUserId()).thenReturn(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        likeService.createPostLike(1L);

        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void deletePostLike_whenLikeNotFound_shouldThrowException() {
        when(userContext.getUserId()).thenReturn(1L);
        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(
                LikeNotFoundException.class,
                () -> likeService.deletePostLike(1L)
        );

        verify(likeRepository, never()).delete(any());
    }

    @Test
    void deletePostLike_shouldDeleteLike_whenLikeExist() {
        Like like = new Like();

        when(userContext.getUserId()).thenReturn(1L);
        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.of(like));

        likeService.deletePostLike(1L);

        verify(likeRepository).delete(any(Like.class));
    }

    @Test
    void createCommentLike_whenCommentNotFound_shouldThrowException() {
        when(userContext.getUserId()).thenReturn(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> likeService.createCommentLike(1L)
        );

        verify(likeRepository, never()).save(any());
    }

    @Test
    void createCommentLike_whenFindLike_shouldThrowException() {
        Comment comment = new Comment();
        comment.setId(1L);

        Like like = new Like();

        when(userContext.getUserId()).thenReturn(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.of(like));

        assertThrows(
                AlreadyLikedException.class,
                () -> likeService.createCommentLike(1L)
        );

        verify(likeRepository, never()).save(any());
    }

    @Test
    void createCommentLike_shouldSaveLike_whenLikeDoesNotExistAndCommentExist() {
        Comment comment = new Comment();
        comment.setId(1L);

        when(userContext.getUserId()).thenReturn(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        likeService.createCommentLike(1L);

        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void deleteCommentLike_whenLikeNotFound_shouldThrowException() {
        when(userContext.getUserId()).thenReturn(1L);
        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(
                LikeNotFoundException.class,
                () -> likeService.deleteCommentLike(1L)
        );

        verify(likeRepository, never()).delete(any());
    }

    @Test
    void deleteCommentLike_shouldDeleteLike_whenLikeExist() {
        Like like = new Like();

        when(userContext.getUserId()).thenReturn(1L);
        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.of(like));

        likeService.deleteCommentLike(1L);

        verify(likeRepository).delete(any(Like.class));
    }
}
