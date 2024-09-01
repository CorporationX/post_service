package faang.school.postservice.exception.messages;

import lombok.Getter;

@Getter
public enum ValidationExceptionMessage {

    PUBLISHER_COLLISION(
            "The post cannot be created by both the project %s and the author %s."
    ),
    POST_WITHOUT_PUBLISHER(
            "Post cannot be without a publisher."
    ),
    OUT_TO_DATE_SCHEDULED_TIME(
            "The planned publication time must be greater than the current time."
    ),
    INVALID_POST_CONTENT(
            "Post content cannot be empty or consist entirely of white separators."
    ),
    COLLISION_STATE_OF_UPDATABLE_SCHEDULED_TIME(
            "You cant set and delete scheduled publication times for the same post with ID %s."
    ),
    UPDATABLE_RESOURCE_IS_NULL(
            "Updatable resource is null."
    ),
    STATE_OF_UPDATABLE_RESOURCE_IS_NOT_SET(
            "The resource update state is not set. All fields are null."
    ),
    COLLISION_STATE_UPDATABLE_RESOURCE(
            "The update status of the resource %s cannot be determined due to a collision." +
                    " The resource must either be updated, created, or deleted." // res id
    ),
    POST_MEDIA_LIMIT_EXCEEDED(
            "The maximum %s number of files in a post %s has been exceeded." // limit and post id
    ),
    UPDATABLE_RESOURCE_DOESNT_EXIST(
            "Updatable resource %s does not exist." // res id
    ),
    DELETABLE_RESOURCE_DOESNT_EXIST(
            "Deletable resource %s does not exist." // res id
    )
    ;

    public final String message;

    ValidationExceptionMessage(String message) {
        this.message = message;
    }
}