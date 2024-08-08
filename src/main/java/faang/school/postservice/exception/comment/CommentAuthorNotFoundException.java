package faang.school.postservice.exception.comment;

public class CommentAuthorNotFoundException extends RuntimeException{

    public CommentAuthorNotFoundException(String message) {
        super(message);
    }
}
