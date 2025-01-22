package faang.school.postservice.service;

import faang.school.postservice.exception.AlreadyLikedException;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LikeValidationServiceTest {

    @InjectMocks
    private LikeValidationService likeValidationService;

    @Mock
    private LikeRepository likeRepository;

    @Test
    void validatePostAlreadyLikedTest(){
        Long postId = 1L;
        Long userId = 2L;
        Mockito.when(likeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(true);
        Assertions.assertThrows(
                AlreadyLikedException.class,
                () -> likeValidationService.validatePostAlreadyLiked(userId,postId));
    }

    @Test
    void validatePostNotBeenLikedTest(){
        Long postId = 1L;
        Long userId = 2L;
        Mockito.when(likeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(false);
        Assertions.assertThrows(
                PostNotFoundException.class,
                () -> likeValidationService.validatePostNotBeenLiked(userId,postId));
    }

    @Test
    void validateLikeTargetAllNotNullTest(){
        Long postId = 1L;
        Long commentId = 1L;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> likeValidationService.validateLikeTarget(postId,commentId));
    }

    @Test
    void validateLikeTargetAllNullTest(){
        Long postId = null;
        Long commentId = null;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> likeValidationService.validateLikeTarget(postId,commentId));
    }

    @Test
    void validateCommentAlreadyLikedTest(){
        Long commentId = 1L;
        Long userId = 2L;
        Mockito.when(likeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(true);
        Assertions.assertThrows(
                AlreadyLikedException.class,
                () -> likeValidationService.validateCommentAlreadyLiked(userId,commentId));
    }

    @Test
    void validateCommentNotBeenLikedTest(){
        Long commentId = 1L;
        Long userId = 2L;
        Mockito.when(likeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        Assertions.assertThrows(
                CommentNotFoundException.class,
                () -> likeValidationService.validateCommentNotBeenLiked(userId,commentId));
    }
}