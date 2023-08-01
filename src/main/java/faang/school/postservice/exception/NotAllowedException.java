package faang.school.postservice.exception;

public class NotAllowedException extends RuntimeException {
    public NotAllowedException(String mes) {
        super(mes);
    }
}
