package faang.school.postservice.exception.minio_exceptions;

public class MinioUploadingFileException extends RuntimeException{
    public MinioUploadingFileException(String message) {
        super(message);
    }
}
