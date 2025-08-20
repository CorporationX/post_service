package faang.school.postservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class HeaderNotFoundException extends RuntimeException {

    public HeaderNotFoundException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
