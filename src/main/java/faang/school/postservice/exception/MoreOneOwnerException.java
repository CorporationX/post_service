package faang.school.postservice.exception;

import faang.school.postservice.model.ErrorType;
import lombok.Getter;

public class MoreOneOwnerException extends RuntimeException {
    @Getter
    private ErrorType errorType;

    public MoreOneOwnerException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
