package faang.school.postservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class EntityDeletedException extends RuntimeException {

    public EntityDeletedException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }

}
