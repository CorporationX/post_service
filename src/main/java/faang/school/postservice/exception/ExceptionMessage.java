package faang.school.postservice.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExceptionMessage {
    SERIALIZE_EXCEPTION("Failed to serialize message"),
    ;

    private final String message;

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
