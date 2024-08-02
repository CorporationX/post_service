package faang.school.postservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {

    NULL_ID("Receiving ID is NULL. Enter correct ID.");

    private final String message;
}
