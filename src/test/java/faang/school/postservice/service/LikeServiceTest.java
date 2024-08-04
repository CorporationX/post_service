package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LikeServiceTest {

    @Mock
    LikeRepository likeRepository;
    @Mock
    PostService postService;
    @Mock
    CommentService commentService;
    @Mock
    LikeValidator likeValidator;
    @Spy
    LikeMapper likeMapper;

    @InjectMocks
    LikeService likeService;

    Long postId = 1L;
    Long commentId = 1L;
    Long userId = 1L;
    Post post = new Post();
    Comment comment = new Comment();

    @Test
    void likePostWithInvalidPostIdShouldThrowException() {
        when(postService.findById(postId)).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> likeService.likePost(postId, commentId));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void likePostWithInvalidUserIdOrInvalidPostConditionsShouldThrowException() {
        when(postService.findById(postId)).thenReturn(post);
        doThrow(DataValidationException.class).when(likeValidator).validateUserAndPost(post, userId);
        assertThrows(DataValidationException.class, () -> likeService.likePost(postId, commentId));
    }

    @Test
    void likePostWithValidParametersShouldSaveLike() {
        likeService.likePost(postId, commentId);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void likePostWithValidParametersShouldReturnLikeDto() {
        when(likeRepository.save(any())).thenReturn(new Like());
        likeService.likePost(postId, commentId);
        verify(likeMapper).toDto(any(Like.class));
    }

    @Test
    void unlikePostWhenInvokesShouldDeleteLike() {
        likeService.unlikePost(postId, commentId);
        verify(likeRepository).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void likeCommentWithInvalidCommentIdShouldThrowException() {
        when(commentService.findById(commentId)).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> likeService.likeComment(commentId, userId));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void likeCommentWithInvalidUserIdOrInvalidCommentConditionsShouldThrowException() {
        when(commentService.findById(commentId)).thenReturn(comment);
        doThrow(DataValidationException.class).when(likeValidator).validateUserAndComment(comment, userId);
        assertThrows(DataValidationException.class, () -> likeService.likeComment(postId, commentId));
    }

    @Test
    void likeCommentWithValidParametersShouldSaveComment() {
        likeService.likeComment(commentId, userId);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void likeCommentWithValidParametersShouldReturnCommentDto() {
        when(likeRepository.save(any())).thenReturn(new Like());
        likeService.likeComment(commentId, userId);
        verify(likeMapper).toDto(any(Like.class));
    }

    @Test
    void unlikeCommentWhenInvokesShouldDeleteComment() {
        likeService.unlikeComment(commentId, userId);
        verify(likeRepository).deleteByCommentIdAndUserId(commentId, userId);
    }
}