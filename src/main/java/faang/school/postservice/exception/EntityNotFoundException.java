package faang.school.postservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class EntityNotFoundException extends NonRetryableException {
    public EntityNotFoundException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
