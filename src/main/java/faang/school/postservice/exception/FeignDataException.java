package faang.school.postservice.exception;

public class FeignDataException extends RuntimeException{

    public FeignDataException(String message) {
        super(message);
    }
}
