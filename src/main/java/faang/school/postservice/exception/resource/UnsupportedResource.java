package faang.school.postservice.exception.resource;

import faang.school.postservice.exception.messages.ResourceServiceExceptionMessage;

public class UnsupportedResource extends ResourceProcessingException {
    public UnsupportedResource(String resourceName, String path) {
        super(ResourceServiceExceptionMessage.UNSUPPORTED_RESOURCE, resourceName, path);
    }
}
