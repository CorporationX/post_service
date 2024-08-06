package faang.school.postservice.exception.messages;

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
    REPUBLICATION_POST(
            "Post with ID %s has already been published in %d."
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
                    " The resource must either be updated, created, or deleted."
    ),
    DRAFT_MEDIA_LIMIT_EXCEEDED(
            "The maximum number of files in a draft has been exceeded."
    ),
    POST_MEDIA_LIMIT_EXCEEDED(
            "The maximum number of files in a post %s has been exceeded."
    )
    ;

    public final String msg;

    ValidationExceptionMessage(String message) {
        msg = message;
    }

    public String getMessage() {
        return msg;
    }
}