package faang.school.postservice.exception;

public class DataValidationException extends RuntimeException{
    DataValidationException(MessageError message){
        super(message.getMessage());
    }
    DataValidationException(String message){
        super(message);
    }
}
