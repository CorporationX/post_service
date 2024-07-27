package faang.school.postservice.exception;

public class PostException extends RuntimeException{

    public PostException(ErrorMessage message){
        super(message.getMessage());
    }

    public PostException(String message){
        super(message);
    }
}
