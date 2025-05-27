package faang.school.postservice.validation.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserClientResponseDto;
import faang.school.postservice.exception.like.LikeAlreadyExistsException;
import faang.school.postservice.exception.user.UserNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeValidatorTest {
    private static final long USER_ID = 4L;
    private static final long POST_ID = 2L;
    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeValidator likeValidator;

    private UserClientResponseDto userDto;
    private Like like;

    @BeforeEach
    public void setUp() {
        userDto = new UserClientResponseDto(USER_ID, "user", "user@gmail.com");
        like = new Like();
    }

    @Test
    void testCheckLikeAuthorExist() {
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);

        assertDoesNotThrow(() -> likeValidator.checkLikeAuthorExists(USER_ID));

        verify(userServiceClient).getUserById(USER_ID);
    }

    @Test
    void testCheckLikeAuthorExist_WhenUserNotFound() {
        doThrow(FeignException.NotFound.class).when(userServiceClient).getUserById(USER_ID);

        assertThrows(UserNotFoundException.class, () -> likeValidator.checkLikeAuthorExists(USER_ID));
    }

    @Test
    void testCheckUserHasNoLikeOnPost_likeDoesntExist() {
        when(likeRepository.findByPostIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> likeValidator.checkUserHasNoLikeOnPost(USER_ID, POST_ID));
    }

    @Test
    void testCheckUserHasNoLikeOnPost_likeExists() {
        when(likeRepository.findByPostIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(like));

        assertThrows(LikeAlreadyExistsException.class, () -> likeValidator.checkUserHasNoLikeOnPost(USER_ID, POST_ID));
    }

    @Test
    void testCheckUserHasNoLikeOnComment_likeDoesntExist() {
        when(likeRepository.findByCommentIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> likeValidator.checkUserHasNoLikeOnComment(USER_ID, POST_ID));
    }

    @Test
    void testCheckUserHasNoLikeOnComment_likeExists() {
        when(likeRepository.findByCommentIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(like));

        assertThrows(LikeAlreadyExistsException.class, () -> likeValidator.checkUserHasNoLikeOnComment(USER_ID, POST_ID));
    }
}