package faang.school.postservice.service;

import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeMessagePublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    LikeRepository likeRepository;
    @Mock
    PostService postService;
    @Mock
    CommentService commentService;
    @Mock
    LikeValidator likeValidator;
    @Mock
    LikeMessagePublisher likeMessagePublisher;
    @Spy
    LikeMapper likeMapper;

    @InjectMocks
    LikeService likeService;

    Long postId = 1L;
    Long commentId = 1L;
    Long userId = 1L;
    Post post = new Post();
    Comment comment = new Comment();
    Like like = new Like();

    @BeforeEach
    void setup(){
        like = Like.builder()
                .post(post)
                .userId(userId)
                .build();
    }

    @Test
    void likePostWithInvalidPostIdShouldThrowException() {
        when(postService.getPost(postId)).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> likeService.likePost(postId, commentId));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void likePostWithInvalidUserIdOrInvalidPostConditionsShouldThrowException() {
        when(postService.getPost(postId)).thenReturn(post);
        doThrow(DataValidationException.class).when(likeValidator).validateUserAndPost(post, userId);
        assertThrows(DataValidationException.class, () -> likeService.likePost(postId, commentId));
    }

    @Test
    void likePostWithValidParametersShouldSaveLike() {
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        likeService.likePost(postId, commentId);
        verify(likeMessagePublisher).publish(any(LikeEvent.class));
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void likePostWithValidParametersShouldReturnLikeDto() {
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        likeService.likePost(postId, commentId);
        verify(likeMessagePublisher).publish(any(LikeEvent.class));
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