package faang.school.postservice.exception;

public class AlreadyPublishedException extends RuntimeException {
    public AlreadyPublishedException() {
        super("post already published");
    }
}
