package faang.school.postservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    OWNER_ID_NOT_PRESENT("Invalid owner", "Owner ID is not present"),
    MORE_ONE_OWNER("More one owner", "There may be only one owner");

    private final String errorCode;
    private final String message;
}
