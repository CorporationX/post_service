package faang.school.postservice.exception;

public class PostRequirementsException extends RuntimeException{
    public PostRequirementsException() {
    }

    public PostRequirementsException(String message) {
        super(message);
    }
}
