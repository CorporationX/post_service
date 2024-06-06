package faang.school.postservice.exception.message;

public enum PostOperationExceptionMessage {
    RE_PUBLISHING_POST_EXCEPTION("The post can't be re-published."),
    LIKES_UPDATE_EXCEPTION("Likes of post can't be updated throw post updating."),
    COMMENTS_UPDATE_EXCEPTION("Comments of post can't be updated through post updating."),
    PUBLISHED_DATE_UPDATE_EXCEPTION("Publishing date of post cannot be updated."),
    DELETED_STATUS_UPDATE_EXCEPTION("Deleted status of post cannot be changed through update."),
    RE_DELETING_POST_EXCEPTION("The post cannot be re-deleted.");
    private final String message;

    PostOperationExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
