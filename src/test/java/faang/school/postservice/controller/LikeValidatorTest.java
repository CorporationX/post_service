package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.validator.like.LikeControllerValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {

    @InjectMocks
    LikeControllerValidator likeControllerValidator;

    private final LikeDto likeDto = LikeDto.builder().userId(1L).build();

    @Test
    void addLikeToCommentValidate() {
        assertThrows(DataValidationException.class,
                () -> likeControllerValidator.validate(-22L));
    }

    @Test
    void addLikeToPostValidate() {
        assertThrows(DataValidationException.class,
                () -> likeControllerValidator.validateTwoIds(-22L, 0L));
    }
}