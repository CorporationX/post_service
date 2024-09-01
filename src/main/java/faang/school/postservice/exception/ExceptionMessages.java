package faang.school.postservice.exception;

public class ExceptionMessages {
    private ExceptionMessages(){}

    public static final String INSERTION_STAPLES = "{}";

    public static final String FAILED_PERSISTENCE = "Unable to persist data into database. Please try again.";
    public static final String COMMENT_NOT_FOUND = "Comment cannot be found.";
    public static final String USER_NOT_FOUND = "User cannot be found.";
    public static final String POST_NOT_FOUND = "Post cannot be found.";

    // publisher
    public static final String SERIALIZATION_ERROR = "Error in serializing object: ";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred: ";
    public static final String LIKE_NOT_FOUND_FOR_POST = "No likes found for postId";
    public static final String LIKE_NOT_FOUND_FOR_COMMENT = "No likes found for commentId";
    public static final String TOPIC_PUBLICATION_EXCEPTION = "An error occurred while publishing the topic: ";
    public static final String WRITING_TO_JSON_EXCEPTION = "An error occurred while writing to json: ";

    public static final String RECORD_NOT_EXIST = "%s with id %d doesn't exists";
    public static final String DOUBLE_LIKES_FOR_ONE_OBJECT = "A %s with an id: %d already has a likes by a user with an id: %d";
    public static final String EXCEPTION_FOR_REPEAT_LIKES = "You are trying to put a like with an id: %d that has already been put by a user with an id: %d for a %s with an id: %s";

    public static final String DELETION_ERROR_MESSAGE = "An error occurred while deleting a record. %s %s";

}