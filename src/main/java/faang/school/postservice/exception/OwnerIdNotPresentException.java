package faang.school.postservice.exception;

import faang.school.postservice.model.ErrorType;
import lombok.Getter;

public class OwnerIdNotPresentException extends RuntimeException {
    @Getter
    private ErrorType errorType;

    public OwnerIdNotPresentException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
