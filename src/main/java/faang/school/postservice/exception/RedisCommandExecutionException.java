package faang.school.postservice.exception;

public class RedisCommandExecutionException extends RuntimeException{
    public RedisCommandExecutionException(String message){super(message);}
}
