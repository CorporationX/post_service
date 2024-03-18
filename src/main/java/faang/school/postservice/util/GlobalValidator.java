package faang.school.postservice.util;

import faang.school.postservice.exception.EntityNotFoundException;

import java.util.Optional;

public class GlobalValidator {

    private GlobalValidator() {

    }

    public static <T> T validateOptional(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new EntityNotFoundException(message));
    }
}
