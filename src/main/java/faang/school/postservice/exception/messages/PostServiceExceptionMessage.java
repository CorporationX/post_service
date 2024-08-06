package faang.school.postservice.exception.messages;

import lombok.Getter;

@Getter
public enum PostServiceExceptionMessage {
    POST_DOESNT_EXIST(
            "Post with ID %s doesnt exist"
    ),
    POST_ALREADY_PUBLISHED(
            "Post with ID %s already published"
    ),
    POST_ALREADY_DELETED(
            "Post with ID %s already deleted"
    ),
    REQUESTED_POST_DELETED(
            "The requested post %s has been removed."
    ),
    USER_DOESNT_EXIST(
            "User with id %s does not exist."
    ),
    PROJECT_DOESNT_EXIST(
            "Project with id %s does not exist."
    ),
    ;

    private final String message;

    PostServiceExceptionMessage(String message) {
        this.message = message;
    }

}
