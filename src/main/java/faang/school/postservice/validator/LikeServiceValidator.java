package faang.school.postservice.validator;

import faang.school.postservice.model.Like;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class LikeServiceValidator {

    public void checkDuplicateLike(Optional<Like> optionalLike) {
        optionalLike.ifPresent(like -> {
            throw new IllegalArgumentException("Has already been liked");
        });
    }

    public void checkAvailabilityLike(Optional<Like> likeOptional) {
        if (likeOptional.isEmpty()) {
            throw new IllegalArgumentException("Like was not found using the passed identifier");
        }
    }
}