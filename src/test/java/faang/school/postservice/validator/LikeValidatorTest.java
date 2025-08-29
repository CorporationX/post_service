package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeValidator likeValidator;

    private static final long USER_ID = 1L;
    private static final long COMMENT_ID = 1L;
    private static final long POST_ID = 1L;

    @Test
    @DisplayName("Should throw EntityNotFound when user not found")
    public void likeTrowsWhenUserNotFound() {
        when(userServiceClient.getUser(USER_ID)).thenThrow(FeignException.NotFound.class);

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                likeValidator.ensureUserExists(USER_ID));
    }

    @Test
    @DisplayName("Should throw FeignException when like on post already exists")
    public void likePostThrowsWhenLikeExists() {
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID).isPresent())
                .thenThrow(FeignException.class);

        Assertions.assertThrows(FeignException.class, () ->
                likeValidator.ensureLikeOnPostAbsent(POST_ID, USER_ID));
    }

    @Test
    @DisplayName("Should throw FeignException when like on comment already exists")
    public void likeCommentThrowsWhenLikeExists() {
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID).isPresent())
                .thenThrow(FeignException.class);

        Assertions.assertThrows(FeignException.class, () ->
                likeValidator.ensureLikeOnCommentAbsent(COMMENT_ID, USER_ID));
    }
}
