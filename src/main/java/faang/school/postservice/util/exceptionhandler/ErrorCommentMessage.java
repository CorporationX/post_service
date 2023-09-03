package faang.school.postservice.util.exceptionhandler;

public class ErrorCommentMessage {
    public static String getAuthorIdWasNotFoundMessage(long authorId) {
        return "Author with id " + authorId + " was not found!";
    }

    public static String getCommentWasNotFound(long commentId) {
        return "Comment with id " + commentId + " was not found!";
    }

    public static String getUpdateNotValidMessage() {
        return "You can't change post and author data when editing a comment!";
    }
}
