package faang.school.postservice.exception;

public class FileCorruptedException extends RuntimeException {
    public FileCorruptedException(String message) {
        super(message);
    }
}
