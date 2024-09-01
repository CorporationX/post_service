package faang.school.postservice.exception.messages;

import lombok.Getter;

@Getter
public enum ResourceServiceExceptionMessage {
    RESOURCE_DOESNT_EXIST(
            "Resource with ID %s doesnt exist."
    ),
    RESOURCE_PROCESSING_FAILED(
            "An error occurred while processing the resource %s."
    ),
    UNSUPPORTED_RESOURCE(
            "Unsupported resource %s."
    ),
    GROUP_RESOURCE_PROCESSING_FAILED(
            "An error occurred while processing the group of resource"
    );

    private final String message;

    ResourceServiceExceptionMessage(String message) {
        this.message = message;
    }
}
