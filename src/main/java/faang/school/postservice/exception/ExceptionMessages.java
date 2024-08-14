package faang.school.postservice.exception;

public class ExceptionMessages {
    private ExceptionMessages(){}

    public static final String FAILED_PERSISTENCE = "Unable to persist data into database. Please try again.";
    public static final String COMMENT_NOT_FOUND = "Comment cannot be found.";
    public static final String POST_NOT_FOUND = "Post cannot be found.";

    // publisher
    public static final String SERIALIZATION_ERROR = "Error in serializing object: ";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred: ";
}