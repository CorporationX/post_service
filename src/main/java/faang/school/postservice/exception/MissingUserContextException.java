package faang.school.postservice.exception;

public class MissingUserContextException extends PostServiceException {
    public MissingUserContextException() {
        super("User context is not set. Please ensure x-user-id header is provided.");
    }
}
