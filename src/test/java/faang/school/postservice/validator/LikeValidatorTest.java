package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exceptions.DataAlreadyExistingException;
import faang.school.postservice.exceptions.DataNotExistingException;
import faang.school.postservice.exceptions.SameTimeActionException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeValidatorTest {

    @InjectMocks
    private LikeValidator likeValidator;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeRepository likeRepository;

    private LikeDto likeDto;

    @BeforeEach
    void setUp() {
        likeDto = LikeDto.builder().postId(1L).commentId(1L).userId(1L).build();
    }

    @Test
    void testValidatorThrowsDataNotExistingException() {
        when(userServiceClient.getUser(1L)).thenThrow(FeignException.class);

        DataNotExistingException dataNotExistingException =
                assertThrows(DataNotExistingException.class, () -> likeValidator.validateLike(likeDto));

        assertEquals("User who wants to add like doesn't exist", dataNotExistingException.getMessage());
    }

    @Test
    void testValidatorThrowsSameTimeActionException() {
        SameTimeActionException sameTimeActionException =
                assertThrows(SameTimeActionException.class, () -> likeValidator.validateLike(likeDto));

        assertEquals("Can't add like on post and comment in the same time",
                sameTimeActionException.getMessage());
    }

    @Test
    void testValidatorThrowsDataAlreadyExistingExceptionExistPostLike() {
        long postId = 1L;
        long userId = 1L;
        likeDto.setCommentId(null);

        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(new Like()));
        DataAlreadyExistingException dataAlreadyExistingException =
                assertThrows(DataAlreadyExistingException.class, () -> likeValidator.validateLike(likeDto));

        assertEquals(String.format("Like on postId: %d by user id: %d already exist", postId, userId),
                dataAlreadyExistingException.getMessage());
    }


    @Test
    void testValidatorThrowsDataAlreadyExistingExceptionExistCommentLike() {
        long commentId = 1L;
        long userId = 1L;
        likeDto.setPostId(null);

        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(new Like()));
        DataAlreadyExistingException dataAlreadyExistingException =
                assertThrows(DataAlreadyExistingException.class, () -> likeValidator.validateLike(likeDto));

        assertEquals(String.format("Like on commentId: %d by user id: %d already exist", commentId, userId),
                dataAlreadyExistingException.getMessage());
    }
}