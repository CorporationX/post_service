package faang.school.postservice.exceptions;

public class SameTimeActionException extends DataValidationException{
    public SameTimeActionException(String message) {
        super(message);
    }
}
