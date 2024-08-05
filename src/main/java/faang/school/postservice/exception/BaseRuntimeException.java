package faang.school.postservice.exception;

public class BaseRuntimeException extends RuntimeException {

    public BaseRuntimeException(){
        super();
    }

    public BaseRuntimeException(String foramtString, Object... arguments) {
        this(String.format(foramtString, arguments));
    }

    public BaseRuntimeException(String message) {
        super(message);
    }
}
