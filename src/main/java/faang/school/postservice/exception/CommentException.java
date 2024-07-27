package faang.school.postservice.exception;

import java.util.function.Supplier;

public class CommentException extends RuntimeException {

    public CommentException(ErrorMessage message){
        super(message.getMessage());
    }

    public CommentException(String message){
        super(message);
    }
}
