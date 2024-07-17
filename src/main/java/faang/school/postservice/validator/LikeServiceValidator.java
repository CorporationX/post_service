package faang.school.postservice.validator;

import faang.school.postservice.model.Like;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LikeServiceValidator {

    public void validActiveLike(Optional<Like> like) {
        if (like.isPresent()) {
            throw new IllegalArgumentException("This user already put like"); // сменить IllegalArgumentException
        }
    }
}
