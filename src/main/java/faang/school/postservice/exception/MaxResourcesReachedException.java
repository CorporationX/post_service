package faang.school.postservice.exception;

public class MaxResourcesReachedException extends RuntimeException{
    public MaxResourcesReachedException(String message) {
        super(message);
    }
}
