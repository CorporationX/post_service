package faang.school.postservice.exception.resource;

import faang.school.postservice.exception.BaseRuntimeException;
import faang.school.postservice.exception.messages.ResourceServiceExceptionMessage;

public class ResourceServiceException extends BaseRuntimeException {
    public ResourceServiceException(ResourceServiceExceptionMessage message, Object... args) {
        super(message.getMessage(), args);
    }
}
