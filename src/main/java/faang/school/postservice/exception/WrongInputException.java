package faang.school.postservice.exception;

public class WrongInputException extends RuntimeException{
   public WrongInputException (MessageError message){
        super(message.getMessage());
    }
    public WrongInputException(String message){
        super(message);
    }
}
