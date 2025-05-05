package faang.school.postservice.contants;

public class InfoMessage {
    //INFO Service
    public static final String INFO_CREATE_COMMENT = "Create comment ID: {}\n The user ID: {}\n Post ID: {}\n";
    public static final String INFO_UPDATE_COMMENT = "Update comment ID: {}\n The user ID: {}\n";
    public static final String INFO_GET_COMMENTS = "Retrieved {} comments for post with ID: {}\n";
    public static final String INFO_DELETE_COMMENT = "Delete comment ID: {}\n";
    public static final String INFO_SENT_TOPIC = "Message sent to topic [{}]: {}\n";

    public static final String INFO_BANNED_USER = "User with authorId {} should be banned, they have {} unverified posts.\n";
    public static final String INFO_PUBLISHING_USER = "Publishing user ban event for userId: {}";
    //INFO Controller
    public static final String INFO_START_CONTROLLER_CREATE_COMMENT = "Controller start create comment. The user ID: {}\n Post ID: {}\n";
    public static final String INFO_START_CONTROLLER_UPDATE_COMMENT = "Controller start update comment ID {}\n. The user ID: {}\n";
    public static final String INFO_START_CONTROLLER_GET_COMMENT = "Controller get comments the post ID: {}\n";
    public static final String INFO_START_CONTROLLER_DELETE_COMMENT = "Controller start delete comment ID: {}\n";
}
