package faang.school.postservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class NotResourceOwnerException extends RuntimeException {

    public NotResourceOwnerException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
