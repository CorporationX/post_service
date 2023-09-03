package faang.school.postservice.util.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String mes) {
        super(mes);
    }
}
