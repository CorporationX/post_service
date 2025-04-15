package faang.school.postservice.exception.minio_exceptions;

public class MinioRemovingFileException extends RuntimeException{
    public MinioRemovingFileException(String message) {
        super(message);
    }
}
