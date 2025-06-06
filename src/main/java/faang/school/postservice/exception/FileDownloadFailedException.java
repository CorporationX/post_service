package faang.school.postservice.exception;

public class FileDownloadFailedException extends RuntimeException {
    public FileDownloadFailedException(String message) {

      super(message);
    }
}
