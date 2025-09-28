package faang.school.postservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class MappingJsonException extends NonRetryableException {

    public MappingJsonException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
