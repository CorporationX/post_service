package faang.school.postservice.exсeption;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String message){
        super(message);
    }
}
