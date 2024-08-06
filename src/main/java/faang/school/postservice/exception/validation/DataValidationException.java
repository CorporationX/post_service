package faang.school.postservice.exception.validation;

import faang.school.postservice.exception.BaseRuntimeException;
import faang.school.postservice.exception.messages.ValidationExceptionMessage;

public class DataValidationException extends BaseRuntimeException {
    public DataValidationException(ValidationExceptionMessage validationMsg, Object... args) {
        super(validationMsg.getMessage(), args);
    }
}
