package faang.school.postservice.exception.user_service_client;

import java.util.NoSuchElementException;

public class UserNotFoundException extends NoSuchElementException {
    public UserNotFoundException(String msg) {
        super(msg);
    }
    public UserNotFoundException(long userId) {
        super(String.format("User with id %d not found", userId));
    }
}
