package faang.school.postservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class FailedFeedHeatException extends RuntimeException {
    public FailedFeedHeatException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
