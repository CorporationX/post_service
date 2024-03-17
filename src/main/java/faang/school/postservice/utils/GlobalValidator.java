package faang.school.postservice.utils;

import faang.school.postservice.exception.EntityNotFoundException;

import java.util.Optional;

public class GlobalValidator {

    private GlobalValidator() {

    }

    public static <T> T validateOptional(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new EntityNotFoundException(message));
    }
}
