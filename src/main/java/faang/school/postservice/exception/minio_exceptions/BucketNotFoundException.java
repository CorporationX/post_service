package faang.school.postservice.exception.minio_exceptions;

public class BucketNotFoundException extends RuntimeException{
    public BucketNotFoundException(String message) {
        super(message);
    }

    public static class MinioFileNotFoundException extends RuntimeException{
        public MinioFileNotFoundException(String message) {
            super(message);
        }
    }
}
