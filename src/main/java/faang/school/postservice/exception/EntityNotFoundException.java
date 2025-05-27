package faang.school.postservice.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, long entityId) {
        super(MessageError.ENTITY_NOT_FOUND_EXCEPTION.getMessage(entityName, entityId));
    }

    public EntityNotFoundException(String entityName) {
        super(MessageError.ENTITY_NOT_FOUND_EXCEPTION.getMessage(entityName));
    }
}
