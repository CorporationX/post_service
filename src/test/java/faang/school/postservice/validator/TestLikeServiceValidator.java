package faang.school.postservice.validator;

import faang.school.postservice.model.Like;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestLikeServiceValidator {
    private LikeServiceValidator likeServiceValidator;

    @BeforeEach
    public void setUp() {
        likeServiceValidator = new LikeServiceValidator();
    }

    @DisplayName("Если лайк уже был поставлен")
    @Test
    public void testValidDuplicateLikeWhenLikeAlreadyDelivered() {
        Optional<Like> optionalLike = Optional.of(new Like());
        assertThrows(IllegalArgumentException.class, () -> likeServiceValidator.checkDuplicateLike(optionalLike));
    }

    @Test
    public void testValidDuplicateLikeWhenValid() {
        Optional<Like> optionalLike = Optional.empty();
        assertDoesNotThrow(() -> likeServiceValidator.checkDuplicateLike(optionalLike));
    }

    @DisplayName("Если Optional будет пустой")
    @Test
    public void testCheckAvailabilityLike() {
        Optional<Like> likeOptional = Optional.empty();
        assertThrows(IllegalArgumentException.class, () -> likeServiceValidator.checkAvailabilityLike(likeOptional));
    }
}