package faang.school.postservice.exception;

public class AlreadyDeletedException extends RuntimeException {
    public AlreadyDeletedException() {
        super("post already deleted");
    }
}
