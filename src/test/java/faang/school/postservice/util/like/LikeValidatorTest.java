package faang.school.postservice.util.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {

    @InjectMocks
    private LikeValidator likeValidator;

    @Test
    public void testValidateLikeBothPostIdAndCommentIdAreNull() {
        final long id = 1L;
        final LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .build();

        assertThrows(DataValidationException.class, () -> likeValidator.validateLike(id, likeDto));
    }

    @Test
    public void testValidateLikeBothPostIdAndCommentIdExist() {
        final long id = 1L;
        final LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .postId(5L)
                .commentId(7L)
                .build();

        assertThrows(DataValidationException.class, () -> likeValidator.validateLike(id, likeDto));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "5, NULL",
            "NULL, 7"
    }, nullValues = "NULL")
    public void testValidateTestPostIdAndCommentIdAreInvalid(Long postIdTest, Long commentIdTest) {
        final long id = 1L;
        final LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .postId(postIdTest)
                .commentId(commentIdTest)
                .build();

        assertThrows(DataValidationException.class, () -> likeValidator.validateLike(id, likeDto));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, NULL",
            "NULL, 1"
    }, nullValues = "NULL")
    public void testValidateLikeIsSuccessful(Long postIdTest, Long commentIdTest) {
        final long id = 1L;
        final LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .postId(postIdTest)
                .commentId(commentIdTest)
                .build();

        likeValidator.validateLike(id, likeDto);
    }

    @Test
    public void testValidateUserInvalidUser() {
        final UserDto userDto = UserDto.builder()
                .id(10L)
                .build();
        final LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .build();

        assertThrows(DataValidationException.class, () -> likeValidator.validateUser(userDto, likeDto));
    }

    @Test
    public void testValidateUserIsSuccessful() {
        final UserDto userDto = UserDto.builder()
                .id(2L)
                .build();
        final LikeDto likeDto = LikeDto.builder()
                .userId(2L)
                .build();

        likeValidator.validateUser(userDto, likeDto);
    }
}
