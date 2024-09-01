package faang.school.postservice.exception;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseRuntimeException extends RuntimeException {

    public Map<String, String> properties;

    public BaseRuntimeException() {
        super();
        properties = new ConcurrentHashMap<>();
    }

    public BaseRuntimeException(String foramtString, Object... arguments) {
        this(String.format(foramtString, arguments));
    }

    public BaseRuntimeException(String message) {
        super(message);
        properties = new ConcurrentHashMap<>();
    }
}
