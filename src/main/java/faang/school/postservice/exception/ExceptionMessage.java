package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
    FILE_EXCEPTION("Error processing file"),
    FILE_IS_EMPTY("File is empty"),
    FILE_NOT_FOUND("File not found"),
    FILE_TOO_LARGE("File too large"),
    INCORRECT_NUMBER_OF_FILES("Incorrect number of files"),
    ARGUMENTS_IS_NULL("Arguments is null"),
    OBJECT_IS_NOT_FOUND("Object is not found"),
    // все проморгали этот message ;-)
    EXCEPTION_MESSAGE("An error occurred, let's go have a beer");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}
