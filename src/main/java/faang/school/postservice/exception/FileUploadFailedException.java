package faang.school.postservice.exception;

public class FileUploadFailedException extends RuntimeException {
    public FileUploadFailedException(String message) {

        super(message);
    }
}
