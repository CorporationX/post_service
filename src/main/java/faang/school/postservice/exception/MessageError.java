package faang.school.postservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessageError {
    WRONG_INPUT_DATA("Input data was incorrect"),
    DOES_NOT_EXIST_IN_DB("Does not exist in data base"),
    NOT_ALLOWED_EMPTY_POST_MESSAGE("Not allowed to have empty content in post");
    private final String message;
}
