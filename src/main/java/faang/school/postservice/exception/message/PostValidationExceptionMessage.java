package faang.school.postservice.exception.message;

public enum PostValidationExceptionMessage {
    NULL_VALUED_POST_ID_EXCEPTION("Post must exists in system to be operated."),
    INCORRECT_POST_AUTHOR_EXCEPTION("Post must have only one author and not less."),
    NON_EXISTING_POST_AUTHOR_EXCEPTION("None of post authors exists in system."),
    NON_EXISTING_USER_EXCEPTION("No such user detected in system for passed user id."),
    NON_EXISTING_PROJECT_EXCEPTION("No such project detected in system for passed project id."),
    NON_EXISTING_POST_EXCEPTION("Post for passed id doesn't exist in system."),
    NON_MATCHING_AUTHORS_EXCEPTION("Post author can't be changed.");

    private final String message;

    PostValidationExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}