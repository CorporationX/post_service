package faang.school.postservice.exception.like;

public class LikeAlreadyExistsException extends RuntimeException {

    public LikeAlreadyExistsException(String message) {
        super(message);
    }
}
