package faang.school.postservice.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.LikeDto;
import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeValidatorTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private LikeValidator likeValidator;

    Long userId;
    UserDto userDto = new UserDto(userId, null, null);
    LikeDto likeDto;
    Long postId;
    Long commentId;


    @Test
    void userValidationTest_success() {
        userId = 1L;
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        likeValidator.userValidation(userId);
        verify(userServiceClient, times(1)).getUser(userId);
    }

    @Test
    void userValidationTest_userDoesNotExists() {
        userId = 2L;
        when(userServiceClient.getUser(userId)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> likeValidator.userValidation(userId));
        verify(userServiceClient, times(1)).getUser(userId);
    }

    @Test
    void likeValidationTest_nullUserId() {
        likeDto = new LikeDto(null,null, 1L, null);
        assertThrows(IllegalArgumentException.class, () -> likeValidator.likeValidation(likeDto));
    }

    @Test
    void likeValidationTest_noPostIdOrCommentId() {
        likeDto = new LikeDto(null,1L, null, null);
        assertThrows(IllegalArgumentException.class, () -> likeValidator.likeValidation(likeDto));
    }

    @Test
    void likeValidationTest_bothPostIdAndCommentId() {
        likeDto = new LikeDto(null,1L, 1L, 1L);
        assertThrows(IllegalArgumentException.class, () -> likeValidator.likeValidation(likeDto));
    }

    @Test
    void likeValidationTest_validPost() {
        likeDto = new LikeDto(null,1L, 1L, null);
        assertDoesNotThrow(() -> likeValidator.likeValidation(likeDto));
    }

    @Test
    void likeValidationTest_validComment() {
        likeDto = new LikeDto(null,1L, null, 1L);
        assertDoesNotThrow(() -> likeValidator.likeValidation(likeDto));
    }

    @Test
    void validatePostExistsTest_postExists() {
        postId = 1L;
        when(postRepository.existsById(postId)).thenReturn(true);
        likeValidator.validatePostExists(postId);
        verify(postRepository, times(1)).existsById(postId);
    }

    @Test
    void validatePostExistsTest_postDoesNotExist() {
        postId = 2L;
        when(postRepository.existsById(postId)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> likeValidator.validatePostExists(postId));
        verify(postRepository, times(1)).existsById(postId);
    }

    @Test
    void validateCommentExistsTest_commentExists() {
        commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(true);
        likeValidator.validateCommentExists(commentId);
        verify(commentRepository, times(1)).existsById(commentId);
    }

    @Test
    void validateCommentExistsTest_commentDoesNotExist() {
        commentId = 2L;
        when(commentRepository.existsById(commentId)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> likeValidator.validateCommentExists(commentId));
        verify(commentRepository, times(1)).existsById(commentId);
    }
}