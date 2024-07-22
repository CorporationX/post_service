package faang.school.postservice.validator;

import faang.school.postservice.model.Like;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestLikeServiceValidator {

    @InjectMocks
    private LikeServiceValidator validator;

    @DisplayName("Если лайк уже был поставлен")
    @Test
    public void testValidDuplicateLikeWhenLikeAlreadyDelivered() {
        Optional<Like> optionalLike = Optional.of(new Like());
        assertThrows(IllegalArgumentException.class, () -> validator.validDuplicateLike(optionalLike));
    }

    @Test
    public void testValidDuplicateLikeWhenValid() {
        Optional<Like> optionalLike = Optional.empty();
        assertDoesNotThrow(() -> validator.validDuplicateLike(optionalLike));
    }
}
