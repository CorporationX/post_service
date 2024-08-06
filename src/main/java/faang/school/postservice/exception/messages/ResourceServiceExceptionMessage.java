package faang.school.postservice.exception.messages;

import lombok.Getter;

@Getter
public enum ResourceServiceExceptionMessage {
    RESOURCE_DOESNT_EXIST(
            "Resource with ID %s doesnt exist."
    );

    private final String message;

    ResourceServiceExceptionMessage(String message) {
        this.message = message;
    }
}
