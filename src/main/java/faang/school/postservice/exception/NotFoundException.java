package faang.school.postservice.exception;

public class NotFoundException extends RuntimeException{

    public NotFoundException(ErrorMessage message){
        super(message.getMessage());
    }

    public NotFoundException(String message){
        super(message);
    }
}
