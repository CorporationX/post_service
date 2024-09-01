package faang.school.postservice.exception.resource;

import faang.school.postservice.exception.messages.ResourceServiceExceptionMessage;

public class UnexistentResourceException extends ResourceServiceException {
    public UnexistentResourceException(long resId) {
        super(ResourceServiceExceptionMessage.RESOURCE_DOESNT_EXIST, resId);
    }
}
