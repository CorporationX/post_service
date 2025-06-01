package faang.school.postservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExceptionMapping {
    private Class<? extends Throwable> exceptionClass;
    private org.springframework.http.HttpStatus status;
}
