package faang.school.postservice.exception.minio_exceptions;

public class MinioAccessException extends RuntimeException {
    public MinioAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
