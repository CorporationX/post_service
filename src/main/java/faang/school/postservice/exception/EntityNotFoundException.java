package faang.school.postservice.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String mes) {
        super(mes);
    }
}
