package faang.school.postservice.exception.comment;

// why the fuck is this called a handler...
public class CommentExceptionHandler extends RuntimeException{
    public CommentExceptionHandler(String message) {
        super(message);
    }
}
