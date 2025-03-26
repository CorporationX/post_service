package faang.school.postservice.contants;

public class ErrorMessage {
    public static final String ERROR_NULL_DTO_COMMENT = "CommentDto cant be null";
    public static final String ERROR_NULL_DTO_UPDATE_COMMENT = "CommentUpdateDto cant be null";
    public static final String ERROR_NULL_POST_ID = "PostId cant be null";
    public static final String ERROR_NULL_AUTHOR_ID = "AuthorId cant be null";
    public static final String ERROR_NULL_COMMENT_ID = "Comment ID cant be null";
    public static final String ERROR_NULL_CONTENT = "Content cant be null";
    public static final String ERROR_NULL_ID = "ID cant be null";
    public static final String ERROR_NOT_AUTHOR_COMMENT = "You are not the author of the comment";

    private static final String ERROR_NOT_FOUND_USER = "User with ID %d not found in the system.\n";
    private static final String ERROR_NOT_FOUND_POST = "Post with ID %d not found in the system.\n";
    private static final String ERROR_NOT_FOUND_COMMENT = "Post with ID %d not found in the system.\n";
    private static final String ERROR_OCCURRED_VALIDATING_USER = "Error occurred while validating user with ID: %d\n";
    private static final String ERROR_WRONG_FORMAT_CONTENT = "Content is blank or more %d\n.";

    public static String getErrorWrongFormatContent(int maxLength) {
        return String.format(ERROR_WRONG_FORMAT_CONTENT, maxLength);
    }

    public static String getErrorNotFoundUser(long id) {
        return String.format(ERROR_NOT_FOUND_USER, id);
    }

    public static String getErrorOccurredValidatingUser(long id) {
        return String.format(ERROR_OCCURRED_VALIDATING_USER, id);
    }

    public static String getErrorNotFoundPost(long id) {
        return String.format(ERROR_NOT_FOUND_POST, id);
    }

    public static String getErrorNotFoundComment(long id) {
        return String.format(ERROR_NOT_FOUND_COMMENT, id);
    }
}
