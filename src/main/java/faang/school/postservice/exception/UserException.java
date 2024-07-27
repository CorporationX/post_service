package faang.school.postservice.exception;

public class UserException extends RuntimeException{

    public UserException(ErrorMessage message){
        super(message.getMessage());
    }

    public UserException(String message){
        super(message);
    }
}
