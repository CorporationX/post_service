package faang.school.postservice.exception.post;

public class ProjectNotExistentException extends RuntimeException {
    public ProjectNotExistentException(String message) {
        super(message);
    }
}
