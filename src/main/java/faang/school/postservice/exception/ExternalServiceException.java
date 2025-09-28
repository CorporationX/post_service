package faang.school.postservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class ExternalServiceException extends RetryableException {

    public ExternalServiceException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
