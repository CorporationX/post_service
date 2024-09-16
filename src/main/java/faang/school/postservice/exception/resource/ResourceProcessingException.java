package faang.school.postservice.exception.resource;

import faang.school.postservice.exception.messages.ResourceServiceExceptionMessage;

public class ResourceProcessingException extends ResourceServiceException {

    ResourceProcessingException(ResourceServiceExceptionMessage message, String resourceName, String path) {
        super(message, resourceName);
        this.properties.put("resource_name", resourceName);
        this.properties.put("path", path);
    }

    public ResourceProcessingException(String resourceName, String path) {
        this(ResourceServiceExceptionMessage.RESOURCE_PROCESSING_FAILED, resourceName, path);
    }
}

