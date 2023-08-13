package faang.school.postservice.exception;

public class SameTimeActionException extends RuntimeException{

    public SameTimeActionException(String message) {
        super(message);
    }
}
