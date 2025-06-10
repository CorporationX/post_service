package faang.school.postservice.exception.like;

import jakarta.persistence.EntityNotFoundException;

public class LikeNotFoundException extends EntityNotFoundException {

    public LikeNotFoundException(String message) {
        super(message);
    }

    public LikeNotFoundException(long likeId) {
        super(String.format("Like with id %d not found", likeId));
    }
}
