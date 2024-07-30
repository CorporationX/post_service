package faang.school.postservice.exception;

public class DataDoesNotExistException extends RuntimeException{
    public DataDoesNotExistException(MessageError message){
        super(message.getMessage());
    }
    DataDoesNotExistException(String message){
        super(message);
    }
}
