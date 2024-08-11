package faang.school.postservice.exception;

public class ExceptionMessages {
    private ExceptionMessages(){}

    public static final String FAILED_PERSISTENCE = "Unable to persist data into database. Please try again.";
    public static final String COMMENT_NOT_FOUND = "Comment cannot be found.";
    public static final String USER_NOT_FOUND = "User cannot be found.";
    public static final String POST_NOT_FOUND = "Post cannot be found.";
    public static final String LIKE_NOT_FOUND_FOR_POST = "No likes found for postId";
    public static final String LIKE_NOT_FOUND_FOR_COMMENT = "No likes found for commentId";
}