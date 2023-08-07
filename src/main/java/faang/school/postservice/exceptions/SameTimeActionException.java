package faang.school.postservice.exceptions;

public class SameTimeActionException extends RuntimeException{
    public SameTimeActionException(String message) {
        super(message);
    }
}
