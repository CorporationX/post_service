package faang.school.postservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {

    AUTHOR_ID_NOT_CONFIRMED("Only the author of the comment can make changes to the comment. " +
            "Your ID and the comment author's ID do not match");

    private final String message;
}
