package faang.school.postservice.validator;

import faang.school.postservice.model.Like;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class LikeServiceValidator {

    public void validDuplicateLike(Optional<Like> optionalLike) {
        optionalLike.ifPresent(like -> {
            throw new IllegalArgumentException("This has already been like");
        });
    }
}
