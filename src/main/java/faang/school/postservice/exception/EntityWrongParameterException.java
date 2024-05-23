package faang.school.postservice.exception;

public class EntityWrongParameterException extends RuntimeException{
    public EntityWrongParameterException(String message){
        super(message);
    }
}
