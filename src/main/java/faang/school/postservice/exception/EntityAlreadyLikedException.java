package faang.school.postservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class EntityAlreadyLikedException extends RuntimeException {
    public EntityAlreadyLikedException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
