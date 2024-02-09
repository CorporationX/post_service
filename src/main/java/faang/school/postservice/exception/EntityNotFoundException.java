package faang.school.postservice.exception;

//import org.webjars.NotFoundException;

import org.webjars.NotFoundException;

public class EntityNotFoundException extends NotFoundException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}